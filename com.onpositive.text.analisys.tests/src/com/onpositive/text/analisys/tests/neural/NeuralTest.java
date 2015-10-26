package com.onpositive.text.analisys.tests.neural;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.TestCase;

public class NeuralTest extends TestCase {
	
	@Test
	public void test00() throws Exception {
		new Trainer().train();
	}

}
