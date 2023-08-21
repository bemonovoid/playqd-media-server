package io.playqd.mediaserver.model;

public sealed interface Tuple<L, R> permits Tuple.EmptyTuple, Tuple.TupleImpl {

    L left();

    R right();

    default boolean isEmpty() {
        return !hasLeft() && !hasRight();
    }

    default boolean hasLeft() {
        return left() != null;
    }

    default boolean hasRight() {
        return right() != null;
    }

    static <L, R> Tuple<L, R> from(L left, R right) {
        return new TupleImpl<>(left, right);
    }

    static <L, R> Tuple<L, R> empty() {
        return new EmptyTuple<>();
    }

    record TupleImpl<L, R>(L left, R right) implements Tuple<L, R> {}

    final class EmptyTuple<L, R> implements Tuple<L, R> {

        @Override
        public L left() {
            return null;
        }

        @Override
        public R right() {
            return null;
        }
    }
}
