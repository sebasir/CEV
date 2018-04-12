package net.hpclab.cev.services;

import java.io.IOException;
import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.hpclab.cev.enums.StatusEnum;
import net.hpclab.cev.model.UserSession;

@WebFilter("/admin/*")
public class AuthorizationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);
		String loginURL = request.getContextPath() + Constant.LOGIN_PAGE;
		boolean loggedIn = false;
		boolean userActive = false;
		try {
			UserSession userSession = (UserSession) session.getAttribute(Constant.USER_DATA);
			loggedIn = true;
			userActive = userSession.getUser().getStatus().equals(StatusEnum.ACTIVO.get());
			if (userActive)
				session.setMaxInactiveInterval(Constant.MAX_IDLE_SESSION_LOGGED_IN);
		} catch (Exception e) {
			loggedIn = false;
		}
		boolean loginRequest = request.getRequestURI().equals(loginURL);
		boolean resourceRequest = request.getRequestURI()
				.startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
		boolean ajaxRequest = "partial/ajax".equals(request.getHeader("Faces-Request"));

		if ((loggedIn && userActive) || loginRequest || resourceRequest) {
			if (!resourceRequest) {
				response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
			}
			chain.doFilter(req, res);
		} else if (ajaxRequest) {
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().printf(Constant.AJAX_REDIRECT_XML, loginURL);
		} else {
			response.sendRedirect(loginURL);
		}
	}

	@Override
	public void destroy() {

	}
}
