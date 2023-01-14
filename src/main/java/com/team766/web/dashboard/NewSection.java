package com.team766.web.dashboard;

public class NewSection extends Widget {
	private String m_name;

	public NewSection() {
		this("", DEFAULT_SORT_ORDER);
	}

	public NewSection(String name) {
		this(name, DEFAULT_SORT_ORDER);
	}

	public NewSection(String name, int sortOrder) {
		super(sortOrder);

		m_name = name;
	}

	@Override
	public String render() {
		String page = "<div>";
		if (!m_name.isEmpty()) {
			page += "<h2>" + m_name + "</h2>";
		}
		page += "</div>";
		return page;
	}
	
}
