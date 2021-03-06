package com.onpositive.text.analisys.webview;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.data.TokenSerializer;
import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.TokenRegistry;
import com.onpositive.text.analysis.neural.NeuralParser;
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
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.addUrl("/participles.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		syntaxParser.registerCustomParserClass(NeuralParser.class);
		this.parser = syntaxParser;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		TokenSerializer serializer = new TokenSerializer();
		

		response.setContentType("text/json;charset=UTF-8");
		PrintWriter writer = response.getWriter();
				
		if (request.getParameter("debug") != null) {
			writer.println(serializer.serialize());
			return;
		}
		
		TokenRegistry.clean();		
		boolean euristics = "true".equalsIgnoreCase(request.getParameter("euristics"));
		parser.setUseEuristics(euristics);
		boolean neural = "true".equalsIgnoreCase(request.getParameter("neural"));
		parser.setUseCustomParser(neural);
		parser.setUseEstimator(!euristics || "true".equalsIgnoreCase(request.getParameter("estimator")));
		List<IToken> processed = parser.parse(request.getParameter("query"));
		writer.println(serializer.serialize(new BasicCleaner().clean(processed)));
	}

}
