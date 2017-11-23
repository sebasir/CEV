package net.hpclab.cev.model;

import java.util.ArrayList;

public class TreeHierachyModel {
	private Integer node;
	private Integer level;
	private TreeHierachyModel father;
	private ArrayList<TreeHierachyModel> leaves;

	public TreeHierachyModel() {
		this.leaves = new ArrayList<>();
	}

	public TreeHierachyModel(Integer node, Integer level) {
		this.node = node;
		this.level = level;
		this.leaves = new ArrayList<>();
	}

	public Integer getNode() {
		return node;
	}

	public void setNode(Integer node) {
		this.node = node;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public TreeHierachyModel getFather() {
		return father;
	}

	public void setFather(TreeHierachyModel father) {
		this.father = father;
	}

	public void addNode(TreeHierachyModel subNode) {
		subNode.setFather(this);
		this.leaves.add(subNode);
	}

	public ArrayList<TreeHierachyModel> getLeaves() {
		return leaves;
	}

	public void setLeaves(ArrayList<TreeHierachyModel> leaves) {
		this.leaves = leaves;
	}
	
	@Override
	public String toString() {
		return "[" + node + "] -> (" + leaves + ")";
	}
}