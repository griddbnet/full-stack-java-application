/*
 	Copyright (c) 2019 TOSHIBA Digital Solutions Corporation.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.toshiba.mwcloud.gs.tools.webapi.dto;

import java.util.List;

public class GWQueryOutput {

	/**
	 * List of column information
	 */
	private List<GWTQLColumnInfo> columns;

	/**
	 * List of rows
	 */
	private List<List<Object>> rows;

	/**
	 * Offset of the result of query
	 */
	private int offset;

	/**
	 * Limit of the query
	 */
	private int limit;

	/**
	 * Total rows of the container
	 */
	private long total;

	/**
	 * Get list of column information
	 * 
	 * @return a {@link List} of {@link GWTQLColumnInfo}
	 */
	public List<GWTQLColumnInfo> getColumns() {
		return columns;
	}

	/**
	 * Set the list of column information
	 * 
	 * @param columns
	 *            a {@link List} of {@link GWTQLColumnInfo}
	 */
	public void setColumns(List<GWTQLColumnInfo> columns) {
		this.columns = columns;
	}

	/**
	 * Get list of rows
	 * 
	 * @return a {@link List} of {@link List} of {@link Object}
	 */
	public List<List<Object>> getRows() {
		return rows;
	}

	/**
	 * Set list of rows
	 * 
	 * @param rows
	 *            a {@link List} of {@link List} of {@link Object}
	 */
	public void setRows(List<List<Object>> rows) {
		this.rows = rows;
	}

	/**
	 * Get offset of the query
	 * 
	 * @return offset of the query
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set offset of the query
	 * 
	 * @param offset
	 *            offset of the query
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Get limit of the query
	 * 
	 * @return limit of the query
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Set limit of the query
	 * 
	 * @param limit
	 *            limit of the query
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Get total rows of the container
	 * 
	 * @return total rows of the container
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * Set total rows of the container
	 * 
	 * @param total
	 *            total rows of the container
	 */
	public void setTotal(long total) {
		this.total = total;
	}

}
