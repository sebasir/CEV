/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

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

/**
 * Este servicio se encarga de interceptar la petición de todos los recuros Web
 * que sean accedidos desde una URL específica, obtenida desde el conexto que
 * indentifique al servidor. La regla de negocio define que los usuarios no
 * registrados y que no han ingresado, no pueden acceder a ningun recuso bajo el
 * recurso <tt>/admin/*</tt>.
 * 
 * La aplicación verifica en memoria si existe tal usuario, de manera que
 * almacena un objeto tipo <tt>Users</tt> en el servicio
 * <tt>SessionService</tt>, el cual indicará si el usuario se encuentra activo.
 * De no estarlo lo redirecciona a la pagina de ingreso del CEV.
 * 
 * @since 1.0
 * @author Sebasir
 * @see SessionService
 * @see UserSession
 *
 */

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
			if (session != null)
				SessionService.getInstance().removeUser(session.getId());
			response.sendRedirect(loginURL);
		}
	}

	@Override
	public void destroy() {

	}
}
