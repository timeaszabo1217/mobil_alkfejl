package com.example.furniturewebshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniturewebshop.models.FurnitureItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FurnitureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FurnitureItem> itemList;
    private boolean isAdmin;

    private OnItemEditClickListener editClickListener;
    private OnItemDeleteClickListener deleteClickListener;

    public FurnitureAdapter(List<FurnitureItem> itemList, boolean isAdmin) {
        this.itemList = itemList;
        this.isAdmin = isAdmin;
    }

    public interface OnItemEditClickListener {
        void onEditClick(FurnitureItem item, int position);
    }

    public interface OnItemDeleteClickListener {
        void onDeleteClick(FurnitureItem item, int position);
    }

    public void setOnItemEditClickListener(OnItemEditClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return isAdmin ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture_admin, parent, false);
            return new AdminFurnitureViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture, parent, false);
            return new FurnitureViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FurnitureItem item = itemList.get(position);

        String imageUrl = item.getImageUrl();
        boolean isDefaultImage = "default".equals(imageUrl);

        if (holder instanceof AdminFurnitureViewHolder) {
            AdminFurnitureViewHolder adminHolder = (AdminFurnitureViewHolder) holder;
            adminHolder.name.setText(item.getName());
            adminHolder.category.setText(item.getCategory());
            adminHolder.price.setText(item.getPrice() + " Ft");

            if (isDefaultImage) {
                adminHolder.image.setImageResource(R.drawable.image);
            } else {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(adminHolder.image);
            }

            adminHolder.btnEdit.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(item, position);
                }
            });

            adminHolder.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(item, position);
                }
            });

        } else if (holder instanceof FurnitureViewHolder) {
            FurnitureViewHolder normalHolder = (FurnitureViewHolder) holder;
            normalHolder.name.setText(item.getName());
            normalHolder.category.setText(item.getCategory());
            normalHolder.price.setText(item.getPrice() + " Ft");

            if (isDefaultImage) {
                normalHolder.image.setImageResource(R.drawable.image);
            } else {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.image)
                        .error(R.drawable.image)
                        .into(normalHolder.image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price;
        ImageView image;

        public FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            category = itemView.findViewById(R.id.item_category);
            image = itemView.findViewById(R.id.item_image);
        }
    }

    static class AdminFurnitureViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price;
        ImageView image;
        Button btnEdit, btnDelete;

        public AdminFurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            category = itemView.findViewById(R.id.item_category);
            image = itemView.findViewById(R.id.item_image);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
