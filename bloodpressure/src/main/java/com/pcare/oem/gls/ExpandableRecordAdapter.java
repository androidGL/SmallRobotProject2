/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.pcare.oem.gls;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pcare.bloodpressure.R;
import com.pcare.common.entity.GlucoseEntity;
import com.pcare.common.table.GluTableController;
import com.pcare.common.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;


public class ExpandableRecordAdapter extends BaseExpandableListAdapter {
	private final GlucoseManager mGlucoseManager;
	private final LayoutInflater mInflater;
	private final Context mContext;
	private SparseArray<GlucoseEntity> mRecords;

	public ExpandableRecordAdapter(final Context context, final GlucoseManager manager) {
		mGlucoseManager = manager;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mRecords = manager.getRecords().clone();
	}

	@Override
	public void notifyDataSetChanged() {
		mRecords = mGlucoseManager.getRecords().clone();
		super.notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return mRecords.size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return mRecords.valueAt(groupPosition);
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return mRecords.keyAt(groupPosition);
	}

	@Override
	public View getGroupView(final int position, boolean isExpanded, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.activity_feature_gls_item, parent, false);

			final GroupViewHolder holder = new GroupViewHolder();
			holder.time = view.findViewById(R.id.time);
			holder.details = view.findViewById(R.id.details);
			holder.concentration = view.findViewById(R.id.gls_concentration);
			view.setTag(holder);
		}
		final GlucoseEntity record = (GlucoseEntity) getGroup(position);
		if (record == null)
			return view; // this may happen during closing the activity
		final GroupViewHolder holder = (GroupViewHolder) view.getTag();
		holder.time.setText(CommonUtil.getDateStr(record.getTimeDate()));
		try {
			holder.details.setText(mContext.getResources().getStringArray(R.array.gls_type)[record.getSampleType()]);
		} catch (final ArrayIndexOutOfBoundsException e) {
			holder.details.setText(mContext.getResources().getStringArray(R.array.gls_type)[0]);
		}
		holder.concentration.setText(mContext.getString(R.string.gls_value, record.getGlucoseConcentration() * 100000.0f)+mContext.getString(R.string.gls_unit_mmolpl));

		return view;
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		final GlucoseEntity record = (GlucoseEntity) getGroup(groupPosition);
		int count = 1 + (record.getStatus() != 0 ? 1 : 0); // Sample Location and optional Sensor Status Annunciation
		return count;
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		final Resources resources = mContext.getResources();
		final GlucoseEntity record = (GlucoseEntity) getGroup(groupPosition);
		String tmp;
		switch (childIdToItemId(childPosition, record)) {
			case 0:
				try {
					tmp = resources.getStringArray(R.array.gls_location)[record.getSampleLocation()];
				} catch (final ArrayIndexOutOfBoundsException e) {
					tmp = resources.getStringArray(R.array.gls_location)[0];
				}
				return new Pair<>(resources.getString(R.string.gls_location_title), tmp);
			case 1: { // sensor status annunciation
				final StringBuilder builder = new StringBuilder();
				final int status = record.getStatus();
				for (int i = 0; i < 12; ++i)
					if ((status & (1 << i)) > 0)
						builder.append(resources.getStringArray(R.array.gls_status_annunciation)[i]).append("\n");
				builder.setLength(builder.length() - 1);
				return new Pair<>(resources.getString(R.string.gls_status_annunciation_title), builder.toString());
			}

			default:
				return new Pair<>("Not implemented", "The value exists but is not shown");
			}
	}

	private int childIdToItemId(final int childPosition, final GlucoseEntity record) {
		int itemId = 0;
		int child = childPosition;

		// Location is required
		if (itemId == childPosition)
			return itemId;

		if (++itemId > 0 && record.getStatus() != 0 && --child == 0) return itemId;

		throw new IllegalArgumentException("No item ID for position " + childPosition);
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return groupPosition + childPosition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.activity_feature_gls_subitem, parent, false);
			final ChildViewHolder holder = new ChildViewHolder();
			holder.title = view.findViewById(android.R.id.text1);
			holder.details = view.findViewById(android.R.id.text2);
			view.setTag(holder);
		}
		final Pair<String, String> value = (Pair<String, String>) getChild(groupPosition, childPosition);
		final ChildViewHolder holder = (ChildViewHolder) view.getTag();
		holder.title.setText(value.first);
		holder.details.setText(value.second);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}

	private class GroupViewHolder {
		private TextView time;
		private TextView details;
		private TextView concentration;
	}

	private class ChildViewHolder {
		private TextView title;
		private TextView details;
	}

}
