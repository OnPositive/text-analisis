package com.onpositive.text.analysis.neural;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.*;

public class NeuralConstants {

	@SuppressWarnings("unchecked")
	public static final Class<? extends Grammem>[] USED_PROPS = (Class<? extends Grammem>[]) new Class<?>[] {PartOfSpeech.class, Gender.class, SingularPlural.class, VerbKind.class, TransKind.class };
	
	public static final int TOKEN_WINDOW_SIZE = 3;
	
}
