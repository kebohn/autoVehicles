package grid.impl;

import java.util.ArrayList;
import java.util.List;

import grid.GridWorld;
import grid.GridWorldListener;
import javafx.geometry.Point2D;

abstract class GridWorldFather<T> implements GridWorld<T> {
	private T[][] values;
	private String name;
	private List<GridWorldListener> listeners;
	
	public GridWorldFather() {
		name="";
		listeners= new ArrayList<>();
	}
	@Override
	public void setData(T[][] values, String name) {
		this.values=values;
		this.name = name;
		fireDataChanged();
	}
	@Override
	public void setValue(Point2D coordinates, T value) {
		values[(int) coordinates.getX()][(int) coordinates.getY()]=value;	
	}

	@Override
	public int getWidth() {
		if (values == null || values[0] == null) return 0;
		return values[0].length;
	}

	@Override
	public int getHeight() {
		if (values == null)return 0;
		return values.length;
	}
	
	@Override
	public T getValue(int[] koordinate) {
		return values[koordinate[0]][koordinate[1]];
	}

	@Override
	public String getName() {
		return name;
	}
	
	
	
	@Override
	public void addListener(GridWorldListener listener) {
		listeners.add(listener);
	}
	
	private void fireDataChanged(){
		for (GridWorldListener listener: listeners){
			listener.dataChanged();
		}
	}
	@Override
	public String toString() {
		String out="";
		for (T[] row : values){
			for(T value : row){
				out=out+value+"\t|";
			}
			out+="\n";
		}
		return out;
	}

	
	
}
