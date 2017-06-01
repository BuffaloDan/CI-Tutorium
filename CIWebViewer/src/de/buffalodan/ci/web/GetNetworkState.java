package de.buffalodan.ci.web;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.buffalodan.ci.web.CategorizedData.Categorizer;

/**
 * Servlet implementation class GetNetworkState
 */
@WebServlet("/GetNetworkState")
public class GetNetworkState extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Categorizer TEST_CATEGORIZER = new Categorizer() {

		@Override
		public Color getColor(int catId) {
			Color color = Color.WHITE;
			if (catId == 1) {
				color = Color.PINK;
			} else {
				color = Color.CYAN;
			}
			return color;
		}

		@Override
		public int categorize(double[] data) {
			int catId = -1;
			if (data.length == 1) {
				if (data[0] > 0)
					catId = 1;
				else if (data[0] < 0)
					catId = 2;
			}
			return catId;
		}

		@Override
		public int getRenderMode(int catId) {
			// Areas
			return 4;
		}
	};

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String state = request.getParameter("state");
		boolean pretty = "true".equals(request.getParameter("pretty"));
		if (state == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No state specified");
			return;
		}
		JSONObject json = null;
		if (state.equals("test")) {
			TestData data = NetworkManager.getInstance().getTestData();
			json = new JSONObject(new CategorizedData(data, TEST_CATEGORIZER));
		} else if (state.equals("training")) {
			RBFNetworkTrainingData data = NetworkManager.getInstance().getRbfNetworkTrainingData();
			json = new JSONObject(new CategorizedData(data));
		} else if (state.equals("network")) {
			json = new JSONObject();
			json.put("running", NetworkManager.getInstance().isRunning());
			json.put("run", NetworkManager.getInstance().getCurrentRun());
			json.put("runs", NetworkManager.getInstance().getRuns());
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state specified");
			return;
		}
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		out.print(pretty ? json.toString(2) : json.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
