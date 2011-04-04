package jp.co.umusus.easydao;

import java.util.Map;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AdjustTableModel extends DefaultTableModel{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	public AdjustTableModel() {
		super();

	}


    /**
     *  Adds a row to the end of the model.  The new row will contain
     *  <code>null</code> values unless <code>rowData</code> is specified.
     *  Notification of the row being added will be generated.
     *
     * @param   rowData          optional data of the row being added
     */
    public void addRow(Map<String,Object>rowData) {
        addRow(convertMapToVector(rowData));
    }


    /**
     * Returns a vector that contains the same objects as the array.
     * @param anArray  the array to be converted
     * @return  the new vector; if <code>anArray</code> is <code>null</code>,
     *				returns <code>null</code>
     */
	protected static Vector convertMapToVector(Map<String,Object> map) {
		if (map == null) {
			return null;
		}
		Vector v = new Vector(map.size());
		for (String str : map.keySet()) {
			//System.out.println("\t" + str + ": " + map.get(str));
			v.addElement(map.get(str));
		}
        return v;
    }
}
