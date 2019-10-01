package org.postgresql.core.brushfire;

import javax.servlet.http.HttpServletRequest;

public interface CurrentRequestHolder {

	HttpServletRequest getServletRequest();

	String getPostgresPrefix();

}
