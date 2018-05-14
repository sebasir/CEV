/*
 * Colección Entomológica Virtual
 * Universidad Central
 * High Performance Computing Laboratory
 * Grupo COMMONS.
 * 
 * Sebastián Motavita Medellín
 * 
 * 2017 - 2018
 */

package net.hpclab.cev.model;

import java.util.ArrayList;

/**
 * Este modelo permite tener la información de un mapa abstracto que se usa para
 * crear posteriormente un árbol que se representa en una pantalla. Cada nodo es
 * identificado por un número único, y cada nodo, tiene un identificador único,
 * un nivel, y una lista de modelo de nodos.
 * 
 * @author Sebasir
 * @since 1.0
 * @see ArrayList
 */
public class TreeHierachyModel {

	/**
	 * Identificador único del nodo, representado por un numero
	 */
	private Integer node;

	/**
	 * Nivel del nodo
	 */
	private Integer level;

	/**
	 * Referencia la nodo padre, de este nodo
	 */
	private TreeHierachyModel father;

	/**
	 * Lista de nodos hijos que tiene este nodo
	 */
	private ArrayList<TreeHierachyModel> leaves;

	/**
	 * Constructor que permite definir el listado hijos que tiene un nodo al ser
	 * creado.
	 */
	public TreeHierachyModel() {
		this.leaves = new ArrayList<>();
	}

	/**
	 * Constructor que recibe los componentes a definir, así como inicializar la
	 * lista de nodos hijo.
	 * 
	 * @param node
	 *            Identificador único del nodo
	 * @param level
	 *            Nivel del nodo a definir
	 */
	public TreeHierachyModel(Integer node, Integer level) {
		this.node = node;
		this.level = level;
		this.leaves = new ArrayList<>();
	}

	/**
	 * @return Identificador del nodo
	 */
	public Integer getNode() {
		return node;
	}

	/**
	 * @param node
	 *            Identificador del nodo a definir
	 */
	public void setNode(Integer node) {
		this.node = node;
	}

	/**
	 * @return Nivel del nodo
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            Nivel del nodo a definir
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return El padre del nodo del mismo modelo
	 */
	public TreeHierachyModel getFather() {
		return father;
	}

	/**
	 * @param father
	 *            Padre del nodo a definir del mismo modelo
	 */
	public void setFather(TreeHierachyModel father) {
		this.father = father;
	}

	/**
	 * @param subNode
	 *            Metodo que añade un nodo hijo al nodo actual
	 */
	public void addNode(TreeHierachyModel subNode) {
		subNode.setFather(this);
		this.leaves.add(subNode);
	}

	/**
	 * @return La lista de nodos hijos que tiene este nodo
	 */
	public ArrayList<TreeHierachyModel> getLeaves() {
		return leaves;
	}

	/**
	 * @param leaves
	 *            Lista de nodos hijos a definir para este nodo
	 */
	public void setLeaves(ArrayList<TreeHierachyModel> leaves) {
		this.leaves = leaves;
	}

	@Override
	public String toString() {
		return "[" + node + "] -> (" + leaves + ")";
	}
}
