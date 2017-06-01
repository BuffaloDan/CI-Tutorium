<%@page import="de.buffalodan.ci.web.NetworkManager"%>
<%@ page language="java" contentType="text/plain; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	String methodStr = request.getParameter("sigmamethod");
	if (methodStr == null || methodStr.equals("")) {
		methodStr = "1";
	}
	int method = 0;
	try {
		method = Integer.parseInt(methodStr);
	} catch (NumberFormatException e) {
		method = -1;
	}
	if (method < 1 || method > 5) {
		out.print("Ungültige Methode " + methodStr + "! (1-5 erlaubt");
	} else {
		String rbfsStr = request.getParameter("rbfs");
		if (rbfsStr == null || rbfsStr.equals("")) {
			rbfsStr = "30";
		}
		int rbfs = 0;
		try {
			rbfs = Integer.parseInt(rbfsStr);
		} catch (NumberFormatException e) {
			rbfs = -1;
		}
		if (rbfs < 1 || rbfs > 100) {
			out.print("Ungültige Anzahl an RBF-Units " + rbfsStr + "! (1-100 erlaubt");
		} else {
			if (NetworkManager.getInstance().newRBFNetwork(rbfs, method)) {
				out.print("Netzwerk angelegt!");
			} else {
				out.print("Warten bis Netzwerk fertig ist!");
			}
		}
	}
%>