from threading import Lock
from typing import List, Optional, Tuple, Union, cast

import attr
from arpeggio import (
    NoMatch,
    PTNodeVisitor,
    SemanticActionResults,
    Terminal,
    visit_parse_tree,
)
from arpeggio.cleanpeg import ParserPEG

from . import _segments as segment
from ._helpers import IMPLICIT_ZERO, UNSET, UnsetType
from ._typing import ImplicitZero, Node, PostTag, PreTag, Separator

canonical = r"""
    version = epoch? release pre? post? dev? local? EOF
    epoch = int "!"
    release = int (dot int)*
    pre = pre_tag pre_post_num
    pre_tag = "a" / "b" / "rc"
    post = sep post_tag pre_post_num
    pre_post_num = int
    post_tag = "post"
    dev = sep "dev" int
    local = "+" local_part (sep local_part)*
    local_part = alpha / int
    sep = dot
    dot = "."
    int = r'0|[1-9][0-9]*'
    alpha = r'[0-9]*[a-z][a-z0-9]*'
"""

permissive = r"""
    version = v? epoch? release pre? (post / post_implicit)? dev? local? EOF
    v = "v"
    epoch = int "!"
    release = int (dot int)*
    pre = sep? pre_tag pre_post_num?
    pre_tag = "c" / "rc" / "alpha" / "a" / "beta" / "b" / "preview" / "pre"
    post = sep? post_tag pre_post_num?
    post_implicit = "-" int
    post_tag = "post" / "rev" / "r"
    pre_post_num = sep? int
    dev = sep? "dev" int?
    local = "+" local_part (sep local_part)*
    local_part = alpha / int
    sep = dot / "-" / "_"
    dot = "."
    int = r'[0-9]+'
    alpha = r'[0-9]*[a-z][a-z0-9]*'
"""

_strict_parser = _permissive_parser = None
_parser_create_lock = Lock()


@attr.s(slots=True)
class Sep:
    value: Optional[Separator] = attr.ib()


@attr.s(slots=True)
class Tag:
    value: Union[PreTag, PostTag] = attr.ib()


class VersionVisitor(PTNodeVisitor):  # type: ignore[misc]
    def visit_version(
        self, node: Node, children: SemanticActionResults
    ) -> List[segment.Segment]:
        return list(children)

    def visit_v(self, node: Node, children: SemanticActionResults) -> segment.V:
        return segment.V()

    def visit_epoch(self, node: Node, children: SemanticActionResults) -> segment.Epoch:
        return segment.Epoch(children[0])

    def visit_release(
        self, node: Node, children: SemanticActionResults
    ) -> segment.Release:
        return segment.Release(tuple(children))

    def visit_pre(self, node: Node, children: SemanticActionResults) -> segment.Pre:
        sep1: Union[Separator, None, UnsetType] = UNSET
        tag: Union[PreTag, UnsetType] = UNSET
        sep2: Union[Separator, None, UnsetType] = UNSET
        num: Union[ImplicitZero, int, UnsetType] = UNSET

        for token in children:
            if sep1 is UNSET:
                if isinstance(token, Sep):
                    sep1 = token.value
                elif isinstance(token, Tag):
                    sep1 = None
                    tag = cast(PreTag, token.value)
            elif tag is UNSET:
                tag = token.value
            else:
                assert isinstance(token, tuple)
                assert len(token) == 2
                sep2 = token[0].value
                num = token[1]

        if sep2 is UNSET:
            sep2 = None
            num = IMPLICIT_ZERO

        assert not isinstance(sep1, UnsetType)
        assert not isinstance(tag, UnsetType)
        assert not isinstance(sep2, UnsetType)
        assert not isinstance(num, UnsetType)

        return segment.Pre(sep1=sep1, tag=tag, sep2=sep2, value=num)

    def visit_pre_post_num(
        self, node: Node, children: SemanticActionResults
    ) -> Tuple[Sep, int]:
        # when "pre_post_num = int", visit_int isn't called for some reason
        # I don't understand. Let's call int() manually
        if isinstance(node, Terminal):
            return Sep(None), int(node.value)

        if len(children) == 1:
            return Sep(None), children[0]
        else:
            return cast("Tuple[Sep, int]", tuple(children[:2]))

    def visit_pre_tag(self, node: Node, children: SemanticActionResults) -> Tag:
        return Tag(node.value)

    def visit_post(self, node: Node, children: SemanticActionResults) -> segment.Post:
        sep1: Union[Separator, None, UnsetType] = UNSET
        tag: Union[PostTag, None, UnsetType] = UNSET
        sep2: Union[Separator, None, UnsetType] = UNSET
        num: Union[ImplicitZero, int, UnsetType] = UNSET

        for token in children:
            if sep1 is UNSET:
                if isinstance(token, Sep):
                    sep1 = token.value
                elif isinstance(token, Tag):
                    sep1 = None
                    tag = cast(PostTag, token.value)
            elif tag is UNSET:
                tag = token.value
            else:
                assert isinstance(token, tuple)
                assert len(token) == 2
                sep2 = token[0].value
                num = token[1]

        if sep2 is UNSET:
            sep2 = None
            num = IMPLICIT_ZERO

        assert not isinstance(sep1, UnsetType)
        assert not isinstance(tag, UnsetType)
        assert not isinstance(sep2, UnsetType)
        assert not isinstance(num, UnsetType)

        return segment.Post(sep1=sep1, tag=tag, sep2=sep2, value=num)

    def visit_post_tag(self, node: Node, children: SemanticActionResults) -> Tag:
        return Tag(node.value)

    def visit_post_implicit(
        self, node: Node, children: SemanticActionResults
    ) -> segment.Post:
        return segment.Post(sep1=UNSET, tag=None, sep2=UNSET, value=children[0])

    def visit_dev(self, node: Node, children: SemanticActionResults) -> segment.Dev:
        num: Union[ImplicitZero, int] = IMPLICIT_ZERO
        sep: Union[Separator, None, UnsetType] = UNSET

        for token in children:
            if sep is UNSET:
                if isinstance(token, Sep):
                    sep = token.value
                else:
                    num = token
            else:
                num = token

        if isinstance(sep, UnsetType):
            sep = None

        return segment.Dev(value=num, sep=sep)

    def visit_local(self, node: Node, children: SemanticActionResults) -> segment.Local:
        return segment.Local("".join(str(getattr(c, "value", c)) for c in children))

    def visit_int(self, node: Node, children: SemanticActionResults) -> int:
        return int(node.value)

    def visit_sep(self, node: Node, children: SemanticActionResults) -> Sep:
        return Sep(node.value)


class ParseError(ValueError):
    """Raised when parsing an invalid version number."""


def _get_parser(strict: bool) -> ParserPEG:
    """Ensure the module-level peg parser is created and return it."""
    global _strict_parser, _permissive_parser

    # Each branch below only acquires the lock if the global is unset.

    if strict:
        if _strict_parser is None:
            with _parser_create_lock:
                if _strict_parser is None:
                    _strict_parser = ParserPEG(
                        canonical, root_rule_name="version", skipws=False
                    )

        return _strict_parser
    else:
        if _permissive_parser is None:
            with _parser_create_lock:
                if _permissive_parser is None:
                    _permissive_parser = ParserPEG(
                        permissive,
                        root_rule_name="version",
                        skipws=False,
                        ignore_case=True,
                    )

        return _permissive_parser


def parse(version: str, strict: bool = False) -> List[segment.Segment]:
    parser = _get_parser(strict)

    try:
        tree = parser.parse(version.strip())
    except NoMatch as exc:
        raise ParseError(str(exc)) from None

    return cast("List[segment.Segment]", visit_parse_tree(tree, VersionVisitor()))
