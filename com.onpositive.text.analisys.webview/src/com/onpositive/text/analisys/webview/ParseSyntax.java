package com.onpositive.text.analisys.webview;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.data.TokenSerializer;
import com.onpositive.text.analysis.TokenRegistry;
import com.onpositive.text.analysis.syntax.SyntaxParser;

/**
 * Servlet implementation class ParseSyntax
 */
@WebServlet("/parse")
public class ParseSyntax extends HttpServlet {
	private static final long serialVersionUID = 1L;

	SyntaxParser parser;
	
    /**
     * Default constructor. 
     */
    public ParseSyntax() {
    	CompositeWordnet wn=new CompositeWordnet();
//		wn.addUrl("/numerics.xml");
//		wn.addUrl("/dimensions.xml");
//		wn.addUrl("/modificator-adverb.xml");
//		wn.addUrl("/prepositions.xml");
//		wn.addUrl("/conjunctions.xml");
//		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.parser = syntaxParser;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		TokenSerializer serializer = new TokenSerializer();
		
		TokenRegistry.clean();
		
		parser.parse(request.getParameter("query"));
		
		response.setContentType("text/json");
		writer.println(serializer.serialize());		
	}

}
