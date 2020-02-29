package xyz.protps.mynewsapp.ui;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.protps.mynewsapp.Model.NewsItem;
import xyz.protps.mynewsapp.R;

/**
 * Created by mmakatri on 7/16/2018.
 */
class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // To identify the type of views
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    // Create new variable from OnItemClickListener for news items
    private final OnItemClickListener listener;
    // Create new variable from OnLoadMoreItemClickListener for the footer
    private final OnLoadMoreItemClickListener loadMoreListener;
    // The activity context
    private Context context;
    // Declaration of variable to hold the list of news
    private List<NewsItem> newsList;

    // Constructor
    NewsAdapter(Context context, List<NewsItem> items, OnItemClickListener listener, OnLoadMoreItemClickListener loadMoreListener) {

        // Set the class variables to the value from the constructor
        this.context = context;
        this.newsList = items;
        this.listener = listener;
        this.loadMoreListener = loadMoreListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_FOOTER) {
            // Return the desired view (the footer) by inflating the layout
            return new FooterViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.layout_footer, parent, false));

        } else {
            // Return the desired view (news layout) by inflating the layout
            return new MyViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.news_item, parent, false));

        }
    }

    // The onCreateViewHolder method to build the user interface of this adapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Check if the view is a footer
        if (holder instanceof FooterViewHolder) {
            // Cast the view to FooterViewHolder
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            // Check if there is no more pages
            if (MainActivity.getCurrentPageId() >= MainActivity.getNumberOfPages()) {
                // Disable the button
                footerViewHolder.loadMoreButton.setEnabled(false);
                // Change the color of the text
                footerViewHolder.loadMoreButton.setTextColor(Color.GRAY);
                // Change the text
                footerViewHolder.loadMoreButton.setText(R.string.no_more_news);
            } else {
                // Enable the button
                footerViewHolder.loadMoreButton.setEnabled(true);
                // Change the color of the text
                int color = ContextCompat.getColor(context, R.color.customColor);
                footerViewHolder.loadMoreButton.setTextColor(color);
                // Change the text
                footerViewHolder.loadMoreButton.setText(R.string.load_more);
            }
            // and if the view is a normal view (news item view)
        } else if (holder instanceof MyViewHolder) {
            // Cast the view to MyViewHolder
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            // Get the current newsItem
            NewsItem currentNews = newsList.get(position);

            // Find the news title in the xml layout
            myViewHolder.title.setText(currentNews.getNewsTitle());

            //  Find news section in the xml layout
            myViewHolder.section.setText(currentNews.getNewsSection());

            // If current news has an author name, show it
            if (!currentNews.getNewsAuthor().equals("")) {
                myViewHolder.author.setText(currentNews.getNewsAuthor());

                myViewHolder.author.setVisibility(View.VISIBLE);
                // If not do not show
            } else {
                myViewHolder.author.setVisibility(View.GONE);
            }

            // Display the date of the current news in that TextView
            // and it is not necessary to hide it if there is no date
            myViewHolder.date.setText(currentNews.getNewsDate());

            // If current news has a trail text, show it
            if (!currentNews.getNewsTrailText().equals("")) {
                myViewHolder.trialText.setText(currentNews.getNewsTrailText());

                myViewHolder.trialText.setVisibility(View.VISIBLE);
                // If not do not show
            } else {
                myViewHolder.trialText.setVisibility(View.GONE);
            }

            // If current news has a thumbnail : show it
            if (!currentNews.getNewsThumbnail().equals("")) {
                // Using the library of Picasso to download directly the image from the
                // Url and put this image inside the desired view.
                Picasso.get().load(currentNews.getNewsThumbnail()).
                        into(myViewHolder.newsThumbnail);

                // Make it visible
                myViewHolder.newsThumbnail.setVisibility(View.VISIBLE);

            } else {
                // If not make its visibility to gone
                myViewHolder.newsThumbnail.setVisibility(View.GONE);
            }
        }
    }

    /**
     * This method will get the view type of the item in the given position
     *
     * @param position the item index
     * @return view type
     */
    @Override
    public int getItemViewType(int position) {
        // If the position is the last index , this is the footer
        if (position == newsList.size()) {
            return TYPE_FOOTER;
        } // else it is a normal item
        return TYPE_ITEM;
    }

    /**
     * @return the number of items in this adapter
     */
    @Override
    public int getItemCount() {
        // Check if there is items in the list , add 1 to be the footer item
        if (newsList.size() > 0) {
            return newsList.size() + 1;
        } else {
            return 0;
        }
    }

    /**
     * This method used to clear the adapter and update the UI
     */
    void clear() {
        // Clear the list
        newsList.clear();

        // Notify the adapter
        notifyDataSetChanged();
    }

    /**
     * Swaps the list used by the NewsAdapter for its news data. This method is called by
     * {@link MainActivity} after a load has finished. When this method is called, we assume we have
     * a new set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param listOfNews the new list of news to use as NewsAdapter's data source
     */
    void swapNewData(final List<NewsItem> listOfNews) {
        // Update the list
        newsList = listOfNews;

        // Notify the adapter
        notifyDataSetChanged();
    }

    /**
     * To add the new data to the end of the list and update the view
     *
     * @param listOfNews The new news list to be added
     */
    void addToList(final List<NewsItem> listOfNews) {
        // Appends all of the elements in the specified collection to the end of this list
        newsList.addAll(newsList.size(), listOfNews);

        // Notify the adapter
        notifyDataSetChanged();
    }

    // An interface to handle the click on any item in the recycler view
    public interface OnItemClickListener {
        // Pass the Url as parameter
        void onItemClick(String url);
    }

    // An interface to handle the click on the footer of the recycler view
    public interface OnLoadMoreItemClickListener {
        void onFooterClick(View v);
    }

    // Class to create an object to put it as a footer in the recycler view.
    class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Definition of all views in the layout
        @BindView(R.id.button_load_more)
        Button loadMoreButton;

        FooterViewHolder(View view) {
            super(view);
            // Bind the views
            ButterKnife.bind(this, view);

            // Call a method to set the on click listener for each item in the list
            loadMoreButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Set the listener for this item
            loadMoreListener.onFooterClick(v);
        }
    }

    // class to hold the view to help avoid the re-inflation
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Definition of all views in the layout
        @BindView(R.id.text_view_news_title)
        TextView title;
        @BindView(R.id.text_view_news_author)
        TextView author;
        @BindView(R.id.text_view_news_date)
        TextView date;
        @BindView(R.id.text_view_news_section)
        TextView section;
        @BindView(R.id.text_view_news_trial)
        TextView trialText;
        @BindView(R.id.image_view_news_thumbnail)
        ImageView newsThumbnail;

        MyViewHolder(View itemView) {
            // Call the super constructor
            super(itemView);

            // Bind the views
            ButterKnife.bind(this, itemView);

            // Call a method to set the on click listener for each item in the list
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            // Get the current position of the list
            int adapterPosition = getAdapterPosition();

            // Get the Url from the current item
            String newsUrl = newsList.get(adapterPosition).getNewsUrl();

            // Set the listener for this item
            listener.onItemClick(newsUrl);
        }
    }
}
