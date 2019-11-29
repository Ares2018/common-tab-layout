package zjol.com.cn.tab_layout;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zjrb.core.utils.UIUtils;

import java.util.List;

import zjol.com.cn.tab_layout.cache.PagerCache;

/**
 * 首页 - 新闻ViewPager - Adapter
 *
 * @author a_liYa
 * @date 2017/7/18 09:43.
 */
public abstract class TabPagerAdapter<T> extends MyFragmentPagerAdapter
        implements PagerCache.FactoryPolicy<T> {

    protected List<T> mList;
    protected SlidingTabLayout mTabLayout;

    PagerCache<T> mCache;

    public TabPagerAdapter(FragmentManager fm, @NonNull List<T> list) {
        super(fm);
        this.mList = list;
        mCache = new PagerCache<>(this);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if (mList.size()>position){
            return mCache.getFragment(mList.get(position));
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public View getTabView(int position, ViewGroup parent) {
        View v = mCache.obtainTabViewCache();
        if (v == null) {
            v = UIUtils.inflate(R.layout.module_tab_item_tab_news, parent, false);
        }
        TextView tvTab = (TextView) v.findViewById(R.id.tv_tab);
        tvTab.setText(getPageTitle(position));
        tvTab.setEnabled(getPageEnabled(position));
        mCache.bindAttach(v);
        return v;
    }

    /**
     * 是否页面可用
     *
     * @param position index
     * @return true：开通; 默认 true
     */
    public boolean getPageEnabled(int position) {
        return true;
    }

    /**
     * 更新数据
     *
     * @param newList 新数据
     */
    public void updateDataSet(List<T> newList) {
        if (newList == null) return;
        if (checkIsChange(newList)) { // 有改变，才更新
            mList = newList;
            notifyDataSetChanged();
            setupTabView();
        }
    }

    public T getData(int index) {
        if (mList != null) {
            if (index >= 0 && index < mList.size()) {
                return mList.get(index);
            }
        }
        return null;
    }

    /**
     * 检查数据是否有变化
     *
     * @param newList 新数据
     * @return true：有变化
     */
    private boolean checkIsChange(List<T> newList) {
        if (mList == null || newList == null) return true;
        if (mList.size() != newList.size()) return true;
        for (int i = 0; i < mList.size(); i++) {
            if (isUpdateEntity(newList.get(i), mList.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对比新老数据
     *
     * @param newVal new value
     * @param oldVal old value
     * @return true：需要更新
     */
    protected abstract boolean isUpdateEntity(T newVal, T oldVal);

    /**
     * data id
     *
     * @param data .
     * @return data id
     */
    protected abstract long getId(T data);

    public int getTabOffsetWidth(int index) {
        int sum = 0;
        for (int i = 0; i < index; i++) {
            View tabAt = mTabLayout.getChildAt(i);
            if (tabAt!=null){
                sum += tabAt.getWidth();
            }
        }
        return sum;
    }

    public void setTabLayout(SlidingTabLayout tabLayout) {
        mTabLayout = tabLayout;
        setupTabView();
    }

    public SlidingTabLayout getTabLayout(){
        return mTabLayout;
    }

    private void setupTabView() {
        if (mTabLayout == null) return;
        mTabLayout.notifyDataSetChanged();
//        final int N = mTabLayout.getTabCount();
//        for (int i = 0; i < N; i++) {
//            mTabLayout.getChildAt(i).setview(getTabView(i, mTabLayout));
//        }
    }

    public int indexOfByChannelId(int id) {
        if (mList != null) {
            for (int i = 0; i < mList.size(); i++) {
                if (id == getId(mList.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

}
