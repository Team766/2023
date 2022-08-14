package com.team766.web.dashboard;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public abstract class Widget {
	public static final int DEFAULT_SORT_ORDER = 0;

	private static Map<Widget, Long> c_widgets =
		Collections.synchronizedMap(new WeakHashMap<Widget, Long>());

	public static Iterable<Widget> listWidgets() {
		synchronized (c_widgets) {
			return c_widgets.entrySet().stream()
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		}
	}

	public Widget(int sortOrder) {
		c_widgets.put(this, (((long)sortOrder) << 32) | c_widgets.size());
	}

	public abstract String render();
}
