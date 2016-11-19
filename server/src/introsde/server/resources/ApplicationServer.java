package introsde.server.resources;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.io.IOException;

@ApplicationPath("/rest/*")
@WebFilter(urlPatterns = "/*")
public class ApplicationServer extends Application implements Filter {

    /* By setting the application path to /* every request to the web server is handled by Jersey that will look for a
     suitable resource. By using /rest/*, instead, it is possible to show custom content for every other url
     (e.g. /index.html).
    */
    private String[] restPatterns = new String[]{"/person", "/measureTypes", "/init"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // redirect requests of the form /rest/restPatterns[i] to /restPatterns[i]
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
