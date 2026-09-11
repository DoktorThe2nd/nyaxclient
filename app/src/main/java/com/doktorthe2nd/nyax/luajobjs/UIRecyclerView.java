package com.doktorthe2nd.nyax.luajobjs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UIRecyclerView extends RecyclerView {
    private final Adapter adapter = new Adapter();

    public UIRecyclerView(@NonNull Context context) {
        super(context);
        this.setLayoutManager(new LinearLayoutManager(context));
        this.setAdapter(adapter);
    }

    public int getSize() {
        return adapter.getItemCount();
    }

    public int find(View view) {
        return adapter.find(view);
    }

    public void append(View view) {
        adapter.insert(adapter.getItemCount(), view);
    }
    public void append(List<View> views) {
        adapter.insert(adapter.getItemCount(), views);
    }

    public void prepend(View view) {
        adapter.insert(0, view);
    }
    public void prepend(List<View> views) {
        adapter.insert(0, views);
    }

    public void insert(int idx, View view) {
        adapter.insert(idx, view);
    }
    public void insert(int idx, List<View> views) {
        adapter.insert(idx, views);
    }
    public void remove(int idx) {
        adapter.remove(idx);
    }
    public void move(int from, int to) {
        adapter.move(from, to);
    }

    public void clear() {
        adapter.clear();
    }

    private static class Holder extends ViewHolder {
        public Holder(Context context) {
            super(new FrameLayout(context));
        }
        public FrameLayout getLayout() {
            return (FrameLayout)itemView;
        }
    }

    private static class Adapter extends RecyclerView.Adapter<Holder> {
        private final List<View> views = new ArrayList<>();

        @SuppressLint("NotifyDataSetChanged")
        public void clear() {
            this.views.clear();
            notifyDataSetChanged();
        }

        public void insert(int idx, View view) {
            this.views.add(idx, view);
            notifyItemInserted(idx);
        }
        public void insert(int idx, List<View> views) {
            this.views.addAll(idx, views);
            notifyItemRangeInserted(idx, views.size());
        }
        public void remove(int idx) {
            this.views.remove(idx);
            notifyItemRemoved(idx);
        }
        public void move(int idx, int to) {
            var view = this.views.get(idx);
            this.views.remove(idx);
            this.views.add(to, view);
            notifyItemMoved(idx, to);
        }
        public int find(View view) {
            return this.views.indexOf(view);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.getLayout().removeAllViews();
            holder.getLayout().addView(views.get(position));
        }

        @Override
        public int getItemCount() {
            return views.size();
        }
    }
}
