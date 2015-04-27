package com.onpositive.text.analysis;

import java.util.Collection;
import java.util.HashMap;

public class TokenRegistry {
	private static TokenRegistry instance;
	
	private HashMap<Integer, IToken> registry;
	private int lastId;
	
	private TokenRegistry() {
		registry = new HashMap<Integer, IToken>();
		lastId = 0;
	}
	
	public static int getVacantId() {
		TokenRegistry in = TokenRegistry.getInstance();
		return in.lastId++;
	}
	
	private static TokenRegistry getInstance() {
		TokenRegistry localInstance = instance;
        if (localInstance == null) {
            synchronized (TokenRegistry.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TokenRegistry();
                }
            }
        }
        return instance;
	}
	
	public static IToken get(int id) {
		TokenRegistry in = TokenRegistry.getInstance();
		return in.registry.get(id);
	}
	
	public static void put(IToken token) {
		TokenRegistry in = TokenRegistry.getInstance();
		in.registry.put(token.id(), token);
	}	
	
	public static int length() { 
		return TokenRegistry.getInstance().registry.size();
	}
	
	public static Collection<IToken> list() {
		return TokenRegistry.getInstance().registry.values();
	}

	public static void clean() {
		TokenRegistry in = TokenRegistry.getInstance();
		in.registry.clear();
		in.lastId = 0;
	}
}
