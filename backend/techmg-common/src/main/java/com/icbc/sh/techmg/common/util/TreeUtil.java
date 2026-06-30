package com.icbc.sh.techmg.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeUtil {

    /**
     * Build a tree from a flat list.
     *
     * @param list            flat list of nodes
     * @param idGetter        function to get node id
     * @param parentIdGetter  function to get node parentId
     * @param childrenSetter  consumer to set children on a node
     * @param rootParentId    the value that marks a root node (usually 0L)
     * @param <T>             node type
     * @return list of root nodes with children populated
     */
    public static <T> List<T> buildTree(List<T> list,
                                        Function<T, Long> idGetter,
                                        Function<T, Long> parentIdGetter,
                                        BiConsumer<T, List<T>> childrenSetter,
                                        Long rootParentId) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<T>> childrenMap = list.stream()
                .filter(n -> parentIdGetter.apply(n) != null && !parentIdGetter.apply(n).equals(rootParentId))
                .collect(Collectors.groupingBy(parentIdGetter));

        List<T> roots = list.stream()
                .filter(n -> parentIdGetter.apply(n) == null || parentIdGetter.apply(n).equals(rootParentId))
                .collect(Collectors.toList());

        for (T node : list) {
            List<T> children = childrenMap.get(idGetter.apply(node));
            if (children != null) {
                childrenSetter.accept(node, children);
            }
        }

        return roots;
    }
}
