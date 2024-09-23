from collections import deque
from typing import Any, Dict, Iterable, TypeVar, Union, cast, overload

from ._typing import ImplicitZero

T = TypeVar("T")
R = TypeVar("R")


class UnsetType:
    def __repr__(self) -> str:
        return "UNSET"


UNSET = UnsetType()


class InfinityType:
    def __repr__(self) -> str:
        return "Infinity"

    def __hash__(self) -> int:
        return hash(repr(self))

    def __lt__(self, other: Any) -> bool:
        return False

    def __le__(self, other: Any) -> bool:
        return False

    def __eq__(self, other: Any) -> bool:
        return isinstance(other, self.__class__)

    def __ne__(self, other: Any) -> bool:
        return not isinstance(other, self.__class__)

    def __gt__(self, other: Any) -> bool:
        return True

    def __ge__(self, other: Any) -> bool:
        return True

    def __neg__(self) -> "NegativeInfinityType":
        return NegativeInfinity


Infinity = InfinityType()


class NegativeInfinityType:
    def __repr__(self) -> str:
        return "-Infinity"

    def __hash__(self) -> int:
        return hash(repr(self))

    def __lt__(self, other: Any) -> bool:
        return True

    def __le__(self, other: Any) -> bool:
        return True

    def __eq__(self, other: Any) -> bool:
        return isinstance(other, self.__class__)

    def __ne__(self, other: Any) -> bool:
        return not isinstance(other, self.__class__)

    def __gt__(self, other: Any) -> bool:
        return False

    def __ge__(self, other: Any) -> bool:
        return False

    def __neg__(self) -> InfinityType:
        return Infinity


NegativeInfinity = NegativeInfinityType()


def fixup_module_metadata(module_name: str, namespace: Dict[str, Any]) -> None:
    def fix_one(obj: Any) -> None:
        mod = getattr(obj, "__module__", None)
        if mod is not None and mod.startswith("parver."):
            obj.__module__ = module_name
            if isinstance(obj, type):
                for attr_value in obj.__dict__.values():
                    fix_one(attr_value)

    for objname in namespace["__all__"]:
        obj = namespace[objname]
        fix_one(obj)


@overload
def last(iterable: Iterable[T]) -> T:
    pass


@overload
def last(iterable: Iterable[T], *, default: T) -> T:
    pass


def last(iterable: Iterable[T], *, default: Union[UnsetType, T] = UNSET) -> T:
    try:
        return deque(iterable, maxlen=1).pop()
    except IndexError:
        if default is UNSET:
            raise
        return cast(T, default)


IMPLICIT_ZERO: ImplicitZero = ""
