/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.co.umusus.easydao.dao;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.co.umusus.easydao.AdjustBeanProcessor;
import jp.co.umusus.easydao.AdjustRowProcessor;
import jp.co.umusus.easydao.util.GenericUtil;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;
/**
 *
 * @author nozaki
 */
public abstract class AbstractDao<T> {

    private Connection connection = null;

    private static final Logger logger__ = Logger.getLogger(AbstractDao.class);

    /**
     * エンティティのクラスです。
     */
    private Class<T> entityClass;
    /**
     * データファイルのパスです。
     */
    private String dataFilePath;

    public AbstractDao(){
        Map<TypeVariable<?>, Type> map = GenericUtil
               .getTypeVariableMap(getClass());
        for (Class<?> c = getClass(); c != Object.class; c = c.getSuperclass()) {
            if (c.getSuperclass() == AbstractDao.class) {
                Type type = c.getGenericSuperclass();
                Type[] arrays = GenericUtil.getGenericParameter(type);
                setEntityClass((Class<T>) GenericUtil.getActualClass(arrays[0],
                        map));
                break;
            }
        }
    }

    public AbstractDao(Connection connection){
        if(connection == null){	//NULLの場合は
			//DB接続不可状態
		}else{
			this.connection = connection;
		}
        Map<TypeVariable<?>, Type> map = GenericUtil
               .getTypeVariableMap(getClass());
        for (Class<?> c = getClass(); c != Object.class; c = c.getSuperclass()) {
            if (c.getSuperclass() == AbstractDao.class) {
                Type type = c.getGenericSuperclass();
                Type[] arrays = GenericUtil.getGenericParameter(type);
                setEntityClass((Class<T>) GenericUtil.getActualClass(arrays[0],
                        map));
                break;
            }
        }
    }

    /**
     * コンストラクタです。
     *
     * @param entityClass
     *            エンティティのクラス
     */
    public AbstractDao(Class<T> entityClass) {
        setEntityClass(entityClass);
    }

    /**
	 * List(Bean)で結果を取得する。
	 * @param sql
	 * @param param
	 * @return
	 */
	public List selectList( String sql, Object[] param,Class type) throws Exception{

        RowProcessor rp = new BasicRowProcessor(new AdjustBeanProcessor());
        QueryRunner runner = new QueryRunner();
        ResultSetHandler handler = new BeanListHandler(type,rp);
        /* 結果をリストにして返す */
        return    getList(sql, param, runner, handler);
	}

    /**
	 * List(エンティティ)で結果を取得する。
	 * @param sql
	 * @param param
	 * @return
	 */
	public List<T> selectList( String sql, Object[] param) throws Exception{
        return selectList(sql, param, getEntityClass());
	}
	/**
	 * List(Map)で結果を取得する。
	 * @param sql
	 * @param param
	 * @return
	 */
	public List selectMapList( String sql, Object[] param) throws Exception{
        RowProcessor rp = new AdjustRowProcessor();
        QueryRunner runner = new QueryRunner();
        ResultSetHandler handler = new MapListHandler(rp);
        /* 結果をリストにして返す */
        return    getList(sql, param, runner, handler);
	}
    /**
	 * List(配列)で結果を取得する。
	 * @param sql
	 * @param param
	 * @return
	 */
	public List selectMapArrayList( String sql, Object[] param) throws Exception{
        RowProcessor rp = new BasicRowProcessor(new AdjustBeanProcessor());
        QueryRunner runner = new QueryRunner();
        ResultSetHandler handler = new ArrayListHandler(rp);
        /* 結果をリストにして返す */
        return    getList(sql, param, runner, handler);
	}

    /**
     * 結果をリストにして返す
     * @param sql
     * @param param
     * @param runner
     * @param handler
     * @return
     */
    private List getList(String sql, Object[] param,QueryRunner runner,ResultSetHandler handler) throws Exception{
        List resultList = new ArrayList();
        try {
            if ( param == null ) {
             resultList = (List) runner.query( this.getConnection(), sql, handler );
            } else {
                resultList = ( List ) runner.query( this.getConnection(), sql, param, handler );
            }
            logger__.debug("実行SQL::" + sql);
            if(param != null){
                for (Object object : param) {
                    logger__.debug("パラメータ::" + object);
                }
            }
        } catch (SQLException ex) {
            logger__.error("SQLException", ex);
            throw ex;
        }
        return resultList;
    }

    /**
	 * 結果をtypeに指定したBeanで返却する
	 * @param sql	select * from hoge where hogehoge = ?
	 * @param param
	 * @param type 返却Entity
	 * @return
	 */
	public Object selectOne( String sql, Object[] param,Class type) throws Exception{
        RowProcessor rp = new BasicRowProcessor(new AdjustBeanProcessor());
		QueryRunner runner = new QueryRunner();
		ResultSetHandler handler = new BeanHandler(type, rp);
        //結果を指定してMapにして返す
        return getOne(sql, param, type, runner, handler);
	}
	/**
	 * 結果をEntityで返却する
	 * @param sql	select * from hoge where hogehoge = ?
	 * @param param
	 * @param type 返却Entity
	 * @return
	 */
	public T selectOne( String sql, Object[] param) throws Exception{
        return (T)selectOne(sql, param, getEntityClass());
	}
	/**
	 * 結果をMapで返却する
	 * @param sql	select * from hoge where hogehoge = ?
	 * @param param
	 * @return
	 */
	public Map selectOneMap( String sql, Object[] param) throws Exception{
        RowProcessor rp = new BasicRowProcessor(new AdjustBeanProcessor());
		QueryRunner runner = new QueryRunner();
		MapHandler handler = new MapHandler( rp);
        //結果を指定してMapにして返す
        return (Map)getOne(sql, param, Map.class, runner, handler);
     }

    /**
     * 結果を指定されて型で返す
     * @param sql
     * @param param
     * @param type
     * @param runner
     * @param handler
     * @return
     */
    private Object getOne(String sql, Object[] param,Class type,QueryRunner runner,ResultSetHandler handler) throws Exception{
        Object resultObj = new Object();
        try {
            /* 結果を指定してエンティティにして返す */
            if ( param == null ) {
                resultObj = Class.forName(type.getName()).cast(runner.query( this.getConnection(), sql, handler ));
            } else {
                resultObj = Class.forName(type.getName()).cast(runner.query( this.getConnection(), sql, param, handler ));
            }
        } catch (ClassNotFoundException ex) {
            logger__.error("ClassNotFound", ex);
            throw ex;
        } catch (SQLException ex) {
            logger__.error("SQLException", ex);
            throw ex;
        }
        return resultObj;
    }

    /**
     * INSERT
     * @param param
     * @return
     */
    public abstract int insert(Object[] param) throws Exception;


    /**
     * UPDATE
     * @param param
     * @return
     */
    public abstract int update(Object[] param) throws Exception;


    /**
     * DELETE
     * @param param
     * @return
     */
    public abstract int delete(Object[] param) throws Exception;

	/**
	 * SQL INSERT、UPDATE、または、DELETE
	 * @param sql
	 * @param param
	 * @return -1 エラー
	 */
	public int dml( String sql, Object[] param )  throws Exception{
        int updateCount = 0;
        try {
            QueryRunner runner = new QueryRunner();
            if ( param == null ) {
                updateCount = runner.update(getConnection(), sql);
            } else {
                updateCount = runner.update( getConnection(),sql, param);
            }
        } catch (SQLException ex) {
            logger__.error("SQLException", ex);
            throw ex;
        }
        return updateCount;
    }


    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the entityClass
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
        //カレントディレクトリ
        String currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
        this.setDataFilePath(currentDir + "/" + replace(entityClass.getSimpleName(), ".", "/") + ".csv");
    }


    /**
     * 文字列を置き換えます。
     *
     * @param text
     *            テキスト
     * @param fromText
     *            置き換え対象のテキスト
     * @param toText
     *            置き換えるテキスト
     * @return 結果
     */
    private  String replace(final String text,
            final String fromText, final String toText) {

        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                buf.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                buf.append(text.substring(pos2, pos));
                buf.append(toText);
                pos2 = pos + fromText.length();
            } else {
                buf.append(text.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    /**
     * @return the dataFilePath
     */
    public String getDataFilePath() {
        return dataFilePath;
    }

    /**
     * @param dataFilePath the dataFilePath to set
     */
    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

}
