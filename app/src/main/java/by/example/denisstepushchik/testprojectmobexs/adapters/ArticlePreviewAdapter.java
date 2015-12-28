package by.example.denisstepushchik.testprojectmobexs.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import by.example.denisstepushchik.testprojectmobexs.R;
import by.example.denisstepushchik.testprojectmobexs.data.ArticleDB;
import by.example.denisstepushchik.testprojectmobexs.service.PicassoBigCache;

public class ArticlePreviewAdapter extends RecyclerView.Adapter<ArticlePreviewAdapter.ViewHolder> {

    private final Context mContext;
    private List<ArticleDB> mArticles;
    private OnItemClickListener mOnItemClickListener;
    private int mArticleIconWidth;
    private int mArticleIconHeight;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public ArticlePreviewAdapter(Context context, List<ArticleDB> articles, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mArticles = articles;
        this.mOnItemClickListener = onItemClickListener;
        this.mArticleIconWidth = (int) mContext.getResources().getDimension(R.dimen.article_icon_width);
        this.mArticleIconHeight = (int) mContext.getResources().getDimension(R.dimen.article_icon_height);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_preview, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int positionArticle = getItemCount() - 1 - position;
        ArticleDB article = mArticles.get(positionArticle);

        holder.mArticleTitle.setText(article.getTitle());
        holder.mArticlePubDate.setText(article.getPubDate());
        String iconURL = article.getIconUrl();
        if (iconURL != null) {
            PicassoBigCache.INSTANCE.getPicassoBigCache(mContext)
                    .load(iconURL)
                    .error(R.drawable.no_image)
                    .resize(mArticleIconWidth, mArticleIconHeight)
                    .centerCrop()
                    .into(holder.mArticleIcon);

        }
    }


    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mArticleIcon;
        private TextView mArticleTitle;
        private TextView mArticlePubDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mArticleIcon = (ImageView) itemView.findViewById(R.id.mArticleIcon);
            mArticleTitle = (TextView) itemView.findViewById(R.id.mArticleTitle);
            mArticlePubDate = (TextView) itemView.findViewById(R.id.mArticlePubDate);
        }
    }
}
