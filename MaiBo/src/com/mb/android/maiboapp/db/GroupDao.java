package com.mb.android.maiboapp.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.mb.android.maiboapp.entity.GroupEntity;
import com.tandy.android.fw2.utils.Helper;

/**
 * 
 * function: group表
 *
 * @ author:cgy
 */
public class GroupDao extends BaseDao {
	
	
	public static String TABLE_NAME = "user_group";

	/**
	 * 
	 * function: 创建表 DataBaseOpenHelper->onCreate 调用
	 *
	 *
	 * @ author:cgy
	 */
	public void createTable() {
		
		execSQL("CREATE TABLE user_group" +
				" ( " +
				" id VARCHAR(255)," +
				" name VARCHAR(255)," +
				" c VARCHAR(255)" +
				" );"); 
	}
	
	/**
	 * 判断分组信息是否存在
	 * 
	 * @param id
	 * @return
	 */
	public boolean exist(String id) {
		boolean isExist = false;
		int count = 0;
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(*) from ").append(TABLE_NAME).append(" where id=? ");
		Cursor cursor = rawQuery(sql.toString(), new String[] { id });
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		DataBaseOpenHelper.closeCursor(cursor);
		if (count > 0) {
			isExist = true;
		}
		return isExist;
	}
	
	/**
	 * 添加分组信息
	 * @param scene
	 */
	public void add(String id,String name,String c){
		if (exist(id)) {
			String sql = " delete from " + TABLE_NAME + " where id = "+id;
			execSQL(sql);
		}
		String sql = " insert into " + TABLE_NAME + " values(?, ?, ?)";
		execSQL(sql, new String[] {id ,name,c});
	}
	
	/**
	 * 添加分组列表
	 * @param list
	 */
	public void addGroupList(List<GroupEntity> list){
		if (Helper.isEmpty(list)) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			GroupEntity entity = list.get(i);
			add(entity.getId(), entity.getName(), entity.getC());
		}
	}
	
	/**
	 * 清楚表
	 */
	public void clear() {
		String sql = " delete from " + TABLE_NAME;
		execSQL(sql);
	}
	
	/**
	 * 获得分组列表
	 * @return
	 */
	public ArrayList<GroupEntity> getList() {
		ArrayList<GroupEntity> list = new ArrayList<GroupEntity>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from ").append(TABLE_NAME);
		Cursor cursor = rawQuery(sql.toString(), new String[] {});
		while(cursor.moveToNext()) {
			GroupEntity po = new GroupEntity();
			po.setId(cursor.getString(cursor.getColumnIndex("id")));
			po.setName(cursor.getString(cursor.getColumnIndex("name")));
			po.setC(cursor.getString(cursor.getColumnIndex("c")));
			list.add(po);
		}
		DataBaseOpenHelper.closeCursor(cursor);
		return list;
	}
	
}
