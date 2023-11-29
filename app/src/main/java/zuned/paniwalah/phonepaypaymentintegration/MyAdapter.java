package zuned.paniwalah.phonepaypaymentintegration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    List<MYDataModel> myDataModelList;

    public MyAdapter(Context context, List<MYDataModel> myDataModelList) {
        this.context = context;
        this.myDataModelList = myDataModelList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_itemslist, null);
        return  new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.holder_name.setText(""+myDataModelList.get(position).getAccountHolderName());
        holder.tc_id.setText(""+myDataModelList.get(position).getTransactionId());
        holder.tc_amount.setText(""+myDataModelList.get(position).getAmount());
        holder.utr.setText(""+myDataModelList.get(position).getUtr());


    }

    @Override
    public int getItemCount() {
        return myDataModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        EditText holder_name;
        TextView tc_id,tc_amount,utr;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            holder_name=itemView.findViewById(R.id.holder_name);
            tc_id=itemView.findViewById(R.id.tc_id);
            tc_amount=itemView.findViewById(R.id.tc_amount);
            utr=itemView.findViewById(R.id.utr);



        }
    }
}
