package com.aj.jeez.jr;

public class Node<T> {
	
	private T yellow;
	private T white;
	
	public Node(
			T yellow,
			T white
			) {
		this.yellow=yellow;
		this.white=white;
	}

	public T yellow() {return yellow;}

	public T white() {return white;}
}
