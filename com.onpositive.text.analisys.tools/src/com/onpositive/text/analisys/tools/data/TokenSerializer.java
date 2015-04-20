package com.onpositive.text.analisys.tools.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.TokenRegistry;


public class TokenSerializer {

	public abstract class JSONModel {
		
		public abstract JSONObject toJSON() throws JSONException;
		
		public String toString() { 
			try {
				return toJSON().toString();
			} catch (JSONException e) {
				return null;
			} 
		}
	}
	
	private class TokenGraph extends JSONModel {
	
		private class Edge extends JSONModel {
			private IToken from;
			private IToken to;
			private int type;
			
			public static final int PARENT = 1;
			public static final int CHILD = 2;
			public static final int NEXT = 4;
			public static final int PREVIOUS = 7;
			
			public Edge(IToken from, IToken to, int type) {
				this.from = from;
				this.to = to;
				this.type = type;
			}
			
			private String getType() {
				switch (type) {
					case Edge.PARENT: return "parent";
					case Edge.CHILD: return "child";
					case Edge.NEXT: return "next";
					case Edge.PREVIOUS: return "previous";
					default: return null;
				}
			}
			
			public JSONObject toJSON() throws JSONException {
				JSONObject obj = new JSONObject();
				obj.put("from", from.id());
				obj.put("to", to.id());
				obj.put("type", this.getType());
				
				return obj;
			}
		}
		
		private class Vertex extends JSONModel {
			private IToken data;		
			
			public Vertex(final IToken data) { this.data = data; }
						
			public JSONObject toJSON() throws JSONException {
				JSONObject obj = new JSONObject();
				obj.put("id", data.id());
				obj.put("type", data.getClass().getSimpleName());
				obj.put("subtype", data.getType());
				obj.put("parser", data.getParserName());
				obj.put("value", data.getShortStringValue().trim());
				
				return obj;
			}
		}
		
		private HashMap<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();
		private List<Edge> edges = new ArrayList<Edge>();
		
		public Vertex addVertex(IToken token) {
			Vertex v = new Vertex(token);
			vertices.put(token.id(), v);
			
			return v;
		}
		public Edge addEdge(IToken from, IToken to, int type) {
			Edge e = new Edge(from, to, type);
			edges.add(e);
			return e;
		}
		
		public JSONObject toJSON() throws JSONException {
			JSONObject obj = new JSONObject();
			JSONArray varr = new JSONArray(),
					  earr = new JSONArray();
			
			for (Vertex v: vertices.values())
				varr.put(v.toJSON());
			
			for (Edge e: edges) {
				earr.put(e.toJSON());				
			}			
			
			obj.put("vertices", varr)
			   .put("edges", earr);
			
			return obj;
		}
		
	}
	
	private void go(TokenGraph graph, HashSet<Integer> visited, IToken token) {
		int id = token.id();
		if (visited.contains(id)) return;
		
		graph.addVertex(token);
		visited.add(id);
		
		List<IToken> children = token.getChildren();
		List<IToken> parents = token.getParents();
		List<IToken> nexts = token.getNextTokens();
		List<IToken> prevs = token.getPreviousTokens();
		
		if (children != null) for (IToken ch : children) {
			graph.addEdge(token, ch, TokenGraph.Edge.CHILD);
			go(graph, visited, ch);
		}
		
		if (parents != null) for (IToken prnt : parents) {
			graph.addEdge(token, prnt, TokenGraph.Edge.PARENT);
			go(graph, visited, prnt);
		}
		
		if (nexts != null) for (IToken next: nexts) {
			graph.addEdge(token, next, TokenGraph.Edge.NEXT);
			go(graph, visited, next);
		}
		
		if (prevs != null) for (IToken prev: prevs) {
			graph.addEdge(token, prev, TokenGraph.Edge.PREVIOUS);
			go(graph, visited, prev);
		}
		
		IToken next = token.getNext();
		if (next != null) {
			graph.addEdge(token, next, TokenGraph.Edge.NEXT);
			go(graph, visited, next);
		}
		
		IToken prev = token.getPrevious();
		if (prev != null) {
			graph.addEdge(token, prev, TokenGraph.Edge.PREVIOUS);
			go(graph, visited, prev);
		}	
	}
	
	public String serialize() {
		
		TokenGraph graph = new TokenGraph();
	
		HashSet<Integer> visited = new HashSet<Integer>();
		
		for (IToken token :  TokenRegistry.list()) {
			go (graph, visited, token);
		}		
		
		return graph.toString();
	}
}
