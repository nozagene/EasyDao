package jp.co.umusus.easydao;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.dbutils.BeanProcessor;

/**
 * <p>
 *
 * </p>
 *
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @see BeanProcessor
 *
 * @since
 */
public class AdjustBeanProcessor extends BeanProcessor  {

    @Override
    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd,
            PropertyDescriptor[] props) throws SQLException {

        int cols = rsmd.getColumnCount();
        int columnToProperty[] = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnLabel = rsmd.getColumnLabel(col);	////カラム名ではなく、別名を取得する
            for (int i = 0; i < props.length; i++) {

            	if (equalsColumnProperty(columnLabel, props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }

    /**
     * データベース側のカラム命名規則はアンダーバー区切りの
     * 場合の対応
     * @param colName
     * @param propName
     * @return
     */
    private boolean equalsColumnProperty(String colName, String propName) {
        return colName.replaceAll("_", "").equalsIgnoreCase(propName);
    }

}
