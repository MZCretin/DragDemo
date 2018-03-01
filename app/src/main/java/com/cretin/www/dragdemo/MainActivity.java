package com.cretin.www.dragdemo;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.flowlayout.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<ShowItem> list;
    private FlowAdapter flowAdapter;
    private String[] res = new String[]{"He", "is", "our", "teacher"};

    //记录正确答案的位置
    private int rightIndex = 4;

    private TextView flow_text;
    private RelativeLayout relativelayout;

    //记录最开始的位置
    private float firstX;
    private float firstY;

    //记录开始点击的View中的位置
    private float firstClickX;
    private float firstClickY;

    //记录屏幕差
    private float tempX;
    private float tempY;

    //流式布局LayoutManager
    private FlowLayoutManager flowLayoutManager;

    //存储点的位置
    private List<ItemPositionModel> itemList;

    //长按触发移动单词 记录当前是否可以移动
    private boolean canMove;
    //记录当前被移动单词的中点坐标
    private Point center = new Point();

    //记录被移动视图的大小
    private float mViewWidth;
    private float mViewHeight;

    //记录recyclerview的位置
    private float rvX;
    private float rvY;
    private float rvHeight;

    //记录当前插入的
    private int currSelectIndex = -1;

    //记录当前被移动的块是否在范围内
    private boolean isInArea;

    //视图
    private LinearLayout ll_answer;
    private TextView tv_right;
    private TextView tv_jump;
    private TextView tv_your;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = ( RecyclerView ) findViewById(R.id.recycler_view);
        relativelayout = ( RelativeLayout ) findViewById(R.id.relativelayout);
        flow_text = ( TextView ) findViewById(R.id.flow_text);
        ll_answer = findViewById(R.id.ll_answer);
        tv_right = findViewById(R.id.tv_right);
        tv_your = findViewById(R.id.tv_your);
        tv_jump = findViewById(R.id.tv_jump);

        //设置正确答案
        tv_right.setText("He is not our teacher.");

        flowLayoutManager = new FlowLayoutManager();
        recyclerView.setLayoutManager(flowLayoutManager);
        list = new ArrayList<>();
        itemList = new ArrayList<>();
        for ( int i = 0; i < res.length * 2 + 1; i++ ) {
            if ( i % 2 == 0 ) {
                list.add(new ShowItem("", 1));
            } else {
                list.add(new ShowItem(res[(i - 1) / 2], 0));
            }
        }
        recyclerView.setAdapter(flowAdapter = new FlowAdapter(list));

        flow_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ( itemList.size() != list.size() ) {
                    mViewWidth = v.getWidth();
                    mViewHeight = v.getHeight();
                    rvX = recyclerView.getX();
                    rvY = recyclerView.getY();
                    rvHeight = recyclerView.getHeight();
                    //在此处处理对各个item位置信息的保存
                    for ( int i = 0; i < list.size(); i++ ) {
                        View itemView = flowLayoutManager.findViewByPosition(i);
                        itemList.add(new ItemPositionModel(( int ) (rvX + itemView.getLeft()),
                                ( int ) (rvY + itemView.getTop()), ( int ) (rvX + itemView.getRight()),
                                ( int ) (rvY + itemView.getBottom()), i));
                    }
                }
                canMove = true;
                return true;
            }
        });

        flow_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG, "v x:" + v.getX() + " v y:" + v.getY() + " e x:" + event.getX() + " e y:" + event.getY() + " ee x:" + event.getRawX() + " ee y:" + event.getRawY());
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
//                    Log.e(TAG, "ACTION_DOWN");
                    //记录当前view的位置
                    firstX = v.getX();
                    firstY = v.getY();
                    firstClickX = event.getX();
                    firstClickY = event.getY();
                    tempX = event.getRawX() - event.getX() - firstX;
                    tempY = event.getRawY() - event.getY() - firstY;
                } else if ( event.getAction() == MotionEvent.ACTION_MOVE ) {
                    if ( !canMove )
                        return false;
//                    Log.e(TAG, "ACTION_MOVE  " + (event.getRawX() - firstClickX) + "   " + (event.getRawY() - firstClickY));
                    //移动的时候
                    float positionX = event.getRawX() - firstClickX - tempX;
                    float positionY = event.getRawY() - firstClickY - tempY;
                    v.setX(positionX);
                    v.setY(positionY);

                    //被移动块的中点
                    int centerX = ( int ) (positionX + mViewWidth / 2);
                    int centerY = ( int ) (positionY + mViewHeight / 2);
                    if ( centerY > rvY && centerY < rvHeight + rvY ) {
                        isInArea = true;
                        //计算被移动点的中点
                        center.set(centerX, centerY);
                        //找出最近的点
                        ItemPositionModel point = findPoint();
                        if ( point != null ) {
                            currSelectIndex = point.getPosition();
                            flowAdapter.notifyDataSetChanged();
                        }
                    } else {
                        currSelectIndex = -1;
                        isInArea = false;
                        flowAdapter.notifyDataSetChanged();
                    }
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
//                    Log.e(TAG, "ACTION_UP");
                    //如果在RecyclerView的范围内才处理 否则回退到原地
                    if ( isInArea ) {
                        //添加成功 移除之前的视图
                        v.setVisibility(View.GONE);
                        //检查并设置结果 最好提取出来
                        ShowItem result = new ShowItem((( TextView ) v).getText().toString(), 0);
                        if ( rightIndex == currSelectIndex ) {
                            //正确
                            result.setIsRight(1);
                        } else {
                            //错误
                            result.setIsRight(2);
                        }
                        list.add(currSelectIndex + 1, result);
                        list.add(currSelectIndex + 2, new ShowItem("", 1));
                        //设置文字
                        StringBuffer resultBuffer = new StringBuffer();
                        for ( ShowItem s :
                                list ) {
                            if ( s.getType() == 0 ) {
                                resultBuffer.append(s.des + " ");
                            }
                        }
                        //获取用户答题的结果 这个可以由后台返回
                        String yourAnswer = resultBuffer.substring(0, resultBuffer.length() - 1) + ".";
                        String midStr = (( TextView ) v).getText().toString();
                        int start = yourAnswer.indexOf(midStr);
                        SpannableString sp = new SpannableString(yourAnswer);
                        if ( rightIndex == currSelectIndex ) {
                            //正确
                            sp.setSpan(new ForegroundColorSpan(Color.parseColor("#7CB92F")),
                                    start, start + midStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            //错误
                            sp.setSpan(new ForegroundColorSpan(Color.parseColor("#D62119")),
                                    start, start + midStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        //设置用户答题内容
                        tv_your.setText(sp);
                        //显示结果
                        ll_answer.setVisibility(View.VISIBLE);
                        //设置下一题
                        tv_jump.setText("下一题");
                    } else {
                        //未成功添加抬起的时候回归原地
                        v.setX(firstX);
                        v.setY(firstY);
                    }
                    //
                    canMove = false;
                    currSelectIndex = -1;
                    flowAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    //找出最近的点 只找没有内容的格子
    private ItemPositionModel findPoint() {
        if ( itemList.isEmpty() )
            return null;
        double distance = Math.sqrt(Math.pow((center.x - itemList.get(0).getCenter().x), 2) +
                Math.pow((center.y - itemList.get(0).getCenter().y), 2));
        int index = 0;
        for ( int i = 1; i < itemList.size(); i++ ) {
            if ( i % 2 == 0 ) {
                double temp = Math.sqrt(Math.pow((center.x - itemList.get(i).getCenter().x), 2) +
                        Math.pow((center.y - itemList.get(i).getCenter().y), 2));
//                Log.e(TAG, "距离" + temp);
                if ( temp <= distance ) {
                    distance = temp;
                    index = i;
                }
            }
        }
//        Log.e(TAG, "位置" + index);
        return itemList.get(index);
    }

    class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ShowItem> list;

        public FlowAdapter(List<ShowItem> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if ( viewType == 0 ) {
                //正文内容类型
                return new MyHolder(View.inflate(MainActivity.this, R.layout.flow_item, null));
            } else {
                //占位符类型
                return new MyHolderDivider(View.inflate(MainActivity.this, R.layout.flow_divider, null));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ShowItem showItem = list.get(position);
            if ( showItem != null )
                if ( showItem.getType() == 0 ) {
                    TextView textView = (( MyHolder ) holder).text;
                    textView.setText(list.get(position).des);
                    if ( showItem.getIsRight() == 1 ) {
                        //正确
                        textView.setBackground(getResources().getDrawable(R.drawable.shape_green_fram_round));
                        textView.setTextColor(Color.WHITE);
                    } else if ( showItem.getIsRight() == 2 ) {
                        //错误
                        textView.setBackground(getResources().getDrawable(R.drawable.shape_red_fram_round));
                        textView.setTextColor(Color.WHITE);
                    } else {
                        //按以前的来
                        textView.setBackground(getResources().getDrawable(R.drawable.shape_white_fram_round));
                        textView.setTextColor(Color.parseColor("#444444"));
                    }
                } else {
                    if ( currSelectIndex == position ) {
                        (( MyHolderDivider ) holder).tv_divider.setVisibility(View.VISIBLE);
                    } else {
                        (( MyHolderDivider ) holder).tv_divider.setVisibility(View.GONE);
                    }
                }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TextView text;

            public MyHolder(View itemView) {
                super(itemView);
                text = ( TextView ) itemView.findViewById(R.id.flow_text);
            }
        }

        class MyHolderDivider extends RecyclerView.ViewHolder {

            private TextView tv_divider;

            public MyHolderDivider(View itemView) {
                super(itemView);
                tv_divider = ( TextView ) itemView.findViewById(R.id.tv_divider);
            }
        }
    }
}
