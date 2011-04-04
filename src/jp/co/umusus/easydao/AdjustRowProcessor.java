package jp.co.umusus.easydao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;



public class AdjustRowProcessor extends BasicRowProcessor{

	@Override
	public Map toMap(ResultSet rs) throws SQLException {
        Map result = new CaseInsensitiveHashMapAdjust();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
        	String columnLabel = rsmd.getColumnLabel(i);	//カラム名ではなく、別名を取得する
        	//System.out.println("\t" + columnLabel );
            result.put(columnLabel, rs.getObject(i));
        }

        return result;
    }

	/**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because
     * databases don't consistenly handle the casing of column names.
     */
    private static class CaseInsensitiveHashMapAdjust extends HashMap {

        /**
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#get(java.lang.Object)
         */
        @Override
        public Object get(Object key) {
            return super.get(key.toString().toLowerCase());
        }

        /**
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public Object put(Object key, Object value) {
            return super.put(key.toString().toLowerCase(), value);
        }

        /**
         * @see java.util.Map#putAll(java.util.Map)
         */
        @Override
        public void putAll(Map m) {
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = m.get(key);
                this.put(key, value);
            }
        }

        /**
         * @see java.util.Map#remove(java.lang.Object)
         */
        @Override
        public Object remove(Object key) {
            return super.remove(key.toString().toLowerCase());
        }
    }
}
