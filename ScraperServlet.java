import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class ScrapeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", ++visitCount);

        String url = request.getParameter("url");
        String[] opts = request.getParameterValues("option");
        List<String> options = opts != null ? Arrays.asList(opts) : new ArrayList<>();

        Map<String, List<String>> result = WebScraper.scrape(url, options);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<h2>You have visited this page " + visitCount + " times.</h2>");
        out.println("<h3>Scraped Results from: " + url + "</h3>");
        out.println("<table border='1'><tr><th>Type</th><th>Data</th></tr>");

        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            for (String item : entry.getValue()) {
                out.println("<tr><td>" + entry.getKey() + "</td><td>" + item + "</td></tr>");
            }
        }

        out.println("</table>");
        out.println("<br><button onclick=\"downloadCSV()\">Download CSV</button>");

        Gson gson = new Gson();
        String json = gson.toJson(result);
        out.println("<h3>JSON Result:</h3><pre>" + json + "</pre>");

        out.println("<script>");
        out.println("function downloadCSV() {");
        out.println("  let csv = 'Type,Data\\n';");
        out.println("  const rows = document.querySelectorAll('table tr');");
        out.println("  for (let i = 1; i < rows.length; i++) {");
        out.println("    const cols = rows[i].querySelectorAll('td');");
        out.println("    csv += cols[0].innerText + ',' + cols[1].innerText.replace(/\\n/g, ' ') + '\\n';");
        out.println("  }");
        out.println("  const blob = new Blob([csv], { type: 'text/csv' });");
        out.println("  const url = window.URL.createObjectURL(blob);");
        out.println("  const a = document.createElement('a');");
        out.println("  a.setAttribute('href', url);");
        out.println("  a.setAttribute('download', 'scraped_data.csv');");
        out.println("  a.click();");
        out.println("}");
        out.println("</script>");
    }
}
