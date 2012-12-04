package de.squig.plc.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.squig.plc.logic.elements.CircuitElement;
import de.squig.plc.logic.helper.Coordinates;

public class CircuitMap {
	protected int height;
	protected int width;

	protected CircuitElement[][] map;
	protected List<CircuitElement> elements;

	public CircuitMap(int width, int height) {
		this.width = width;
		this.height = height;
		map = new CircuitElement[width][height];
		elements = new ArrayList<CircuitElement>();
	}

	public CircuitElement getElementAt(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		if (x >= width || y >= height)
			return null;
		return map[x][y];
	}

	public CircuitElement getElementAt(Coordinates coords) {
		return getElementAt(coords.getX(), coords.getY());
	}

	public Coordinates findElement(CircuitElement element) {
		if (element == null)
			return null;
		int x = 0;
		for (CircuitElement[] map1 : map) {
			int y = 0;
			for (CircuitElement map2 : map1) {
				if (map2 == element)
					return new Coordinates(x, y);
				y++;
			}
			x++;
		}
		return null;
	}

	public boolean addElement(CircuitElement element, int x, int y) {
		return addElement(element, x, y, false);
	}

	public boolean addElement(CircuitElement element, int x, int y,
			boolean updateNeighbours) {
		if (x < 0 && y < 0) {
			if (element.getMapX() >= 0 && element.getMapY() >= 0) {
				x = element.getMapX();
				y = element.getMapY();
			} else
				return false;
		} else {
			element.setMapX(x);
			element.setMapY(y);
		}

		if (map.length < x+1)
			return false;
		if (map[x].length < y+1)
			return false;

		if (getElementAt(x, y) != null)
			return false;
		if (findElement(element) != null)
			return false;


		map[x][y] = element;
		elements.add(element);

		if (updateNeighbours) {
			CircuitElement el;
			if ((el = getElementAt(x + 1, y)) != null)
				el.onNeighboursUpdate();
			if ((el = getElementAt(x - 1, y)) != null)
				el.onNeighboursUpdate();
			if ((el = getElementAt(x, y + 1)) != null)
				el.onNeighboursUpdate();
			if ((el = getElementAt(x, y - 1)) != null)
				el.onNeighboursUpdate();
		}

		return true;
	}

	public void removeElement(CircuitElement element) {
		map[element.getMapX()][element.getMapY()] = null;
		elements.remove(element);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void empty() {
		Iterator<CircuitElement> it = getAllElement().iterator();
		while (it.hasNext()) {
			CircuitElement el = it.next();
			map[el.getMapX()][el.getMapY()] = null;
		}
		elements.clear();
	}

	public List<CircuitElement> getAllElement() {
		return elements;
	}

	public List<CircuitElement> getChangedElements(boolean reset) {
		List<CircuitElement> result = new ArrayList<CircuitElement>();
		for (CircuitElement ele : elements) {
			if (ele.isChanged()) {
				result.add(ele);
				if (reset)
					ele.setChanged(false);
			}
		}
		return result;
	}

	public void removeDeleted() {
		List<CircuitElement> toremove = new ArrayList<CircuitElement>();
		for (CircuitElement element :  getAllElement())
			if (element.getType() == CircuitElement.TYPES.DELETED)
				toremove.add(element);
		for (CircuitElement element : toremove) {
			map[element.getMapX()][element.getMapY()] = null;
			elements.remove(element);
		}
	}

}
