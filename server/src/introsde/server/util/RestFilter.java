package introsde.server.util;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class RestFilter implements Filter {

    private String[] restPatterns = new String[] { "/person", "/measureTypes"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            String path = ((HttpServletRequest) request).getServletPath();
            for (String prefix : restPatterns) {
                if (path.startsWith(prefix)) {
                    request.getRequestDispatcher("/rest" + path).forward(request, response);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}