from typing import Optional, Tuple, Union

import attr

from ._helpers import UnsetType
from ._typing import ImplicitZero, PostTag, PreTag, Separator


@attr.s(slots=True)
class Segment:
    pass


@attr.s(slots=True)
class V(Segment):
    pass


@attr.s(slots=True)
class Epoch:
    value: int = attr.ib()


@attr.s(slots=True)
class Release:
    value: Tuple[int, ...] = attr.ib()


@attr.s(slots=True)
class Pre:
    value: Union[ImplicitZero, int] = attr.ib()
    sep1: Optional[Separator] = attr.ib()
    tag: PreTag = attr.ib()
    sep2: Optional[Separator] = attr.ib()


@attr.s(slots=True)
class Post:
    value: Union[ImplicitZero, int] = attr.ib()
    sep1: Union[Separator, UnsetType, None] = attr.ib()
    tag: Optional[PostTag] = attr.ib()
    sep2: Union[Separator, UnsetType, None] = attr.ib()


@attr.s(slots=True)
class Dev:
    value: Union[ImplicitZero, int] = attr.ib()
    sep: Optional[Separator] = attr.ib()


@attr.s(slots=True)
class Local:
    value: str = attr.ib()
