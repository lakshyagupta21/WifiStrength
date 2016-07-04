package dexter.com.wifistrength;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dexter on 7/5/16.
 */
public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.MyViewHolder> {

    private List<WifiSignal> legendList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title ;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.legendName);
        }
    }


    public LegendAdapter(List<WifiSignal> fileList) {
        this.legendList = fileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String title =legendList.get(position).SSID;
        holder.title.setText(title);
        holder.title.setTextColor(legendList.get(position).getColor());
        Log.e("ADDED",title);
    }

    @Override
    public int getItemCount() {
        return legendList.size();
    }
}