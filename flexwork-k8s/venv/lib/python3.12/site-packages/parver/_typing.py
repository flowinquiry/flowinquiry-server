import sys
from typing import Literal, Union

from arpeggio import NonTerminal, Terminal

if sys.version_info >= (3, 10):
    from typing import TypeAlias
else:
    from typing_extensions import TypeAlias

PreTag: TypeAlias = Literal["c", "rc", "alpha", "a", "beta", "b", "preview", "pre"]
NormalizedPreTag: TypeAlias = Literal["a", "b", "rc"]
Separator: TypeAlias = Literal[".", "-", "_"]
PostTag: TypeAlias = Literal["post", "rev", "r"]

ImplicitZero: TypeAlias = Literal[""]

Node: TypeAlias = Union[Terminal, NonTerminal]
