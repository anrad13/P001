package ru.anrad.p001.core;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.anrad.p001.R;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder>{
    public List<Duty> records;
    private ItemListener itemListener;

    public interface ItemListener {
        void onItemClick(Duty duty);
    }

    class ItemClickListener implements View.OnClickListener {
        private Duty record;

        @Override
        public void onClick(View v) {
            //Snackbar.make( v, "кликнули!!!: " + record.toString(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            itemListener.onItemClick(record);
        }

        public void setRecord(Duty duty) {
            record = duty;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemLayout;
        private TextView itemWhat;
        private TextView itemWho;
        private TextView itemWhen;
        private ItemClickListener itemClickListener;
        private Duty duty;

        public ViewHolder(View itemView) {
            super(itemView);
            itemClickListener = new ItemClickListener();
            itemView.setOnClickListener(itemClickListener);

            itemLayout = (RelativeLayout) itemView.findViewById(R.id.list_item_card_view_layout);
            itemWhat = (TextView) itemView.findViewById(R.id.list_item_card_view_what);
            itemWho = (TextView) itemView.findViewById(R.id.list_item_card_view_who);
            itemWhen = (TextView) itemView.findViewById(R.id.list_item_card_view_when);
        }

        public Duty getDuty() {
            return duty;
        }

        public void setDuty (@NonNull Duty d) {
            duty = d;
            //Log.v("RVAdapter", "onBindViewHolder: item = " + d.toString());
            itemLayout.setBackgroundColor(ContextCompat.getColor(itemLayout.getContext(), ItemColor.getColor(d)));

            if (d.isWho()) {
                itemWho.setText(d.getWho());
                itemWho.setVisibility(View.VISIBLE);
            }
            else {
                itemWho.setVisibility(View.GONE);
                //Log.v("RVAdapter", "onBindViewHolder: Who is gone");
            }

            Date date;
            if (d.getState().equals(Duty.ACTIVE)) {
                date = d.getWhen();
            } else {
                date = d.getTimestamp();
            }

            if (date != null) {
                itemWhen.setText(WhenDateFormat.format(date));
                itemWhen.setVisibility(View.VISIBLE);
            }
            else {
                itemWhen.setVisibility(View.GONE);
                //Log.v("RVAdapter", "onBindViewHolder: When is gone");
            }

            itemWhat.setText(d.getWhat());
            if ( !d.isWho() && date==null) {
                itemWhat.setMinLines(2);
            }
            else {
                itemWhat.setMinLines(1);
            }

            itemClickListener.setRecord(d);
        }

    }

    public RVAdapter(List<Duty> l, ItemListener il) {
        records = l;
        itemListener = il;
        //
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemlist_cardview, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Duty d = records.get(i);
        viewHolder.setDuty(d);

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public Duty getItem(int itemId) {
        return records.get(itemId);
    }

    public void updateItems (List<Duty> records) {
        this.records = records;
        this.notifyDataSetChanged();
    }

    private static class ItemColor {
       static int getColor (@NonNull Duty duty) {
            if (!duty.isActive()) return R.color.cardview_simple;

            if (!duty.isWhen()) return R.color.cardview_simple;

            Calendar c = Calendar.getInstance();
            c.setTime(duty.getWhen());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);
            yesterday.add(Calendar.DATE, -1);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            tomorrow.add(Calendar.DATE, 1);

            if (c.equals(today)) {
                return R.color.cardview_today;
            } else if (c.equals(tomorrow)) {
                return R.color.cardview_tomorrow;
            } else if (c.before(today)) {
                return R.color.cardview_before_today;
            } else if (c.after(tomorrow)) {
                return R.color.cardview_after_tomorow;
            } else {
                return R.color.cardview_simple;
            }

        }
    }

    private static class WhenDateFormat {
        static final SimpleDateFormat WHEN_DATE_FORMAT = new SimpleDateFormat("EEE d MMM");
        static String format (Date d) {
            if (d == null) return " ";

            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);
            yesterday.add(Calendar.DATE, -1);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            tomorrow.add(Calendar.DATE, 1);

            if (c.equals(today)) {
                return "Сегодня";
            } else if (c.equals(yesterday)) {
                return "Вчера";
            } else if (c.equals(tomorrow)) {
                return "Завтра";
            } else {
                return WHEN_DATE_FORMAT.format(d);
            }

        }
    }


}