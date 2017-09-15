#slidebar
---

  ***example:***

  **xml:**

      <com.x.leo.slidebar.SlideBarWithText2 xmlns:app="http://schemas.android.com/apk/res-auto"
                  android:id="@+id/sb_term"
                  android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:padding="@dimen/dp12"
                  app:leftText="@string/loan_7days"
                  app:leftTextColor="@color/textlblue"
                  app:leftTextSize="@dimen/dp14"
                  app:progressBagColor="@color/colorPrimaryDark"
                  app:progressBottomDistance="@dimen/dp10"
                  app:progressColor="@color/colorProgress_blue"
                  app:progressTipDistance="@dimen/dp10"
                  app:progressbarHeight="10dp"
                  app:rightText="@string/loan_14days"
                  app:rightTextColor="@color/textlblue"
                  app:rightTextSize="@dimen/dp14"
                  app:tipFillColor="@color/colorPrimaryDark"
                  app:tipStrokeColor="@color/colorProgress_blue"
                  app:tipStrokeWidth="2dp"
                  app:tipsDefText="no value"
                  app:tipsTextColor="@color/colorProgress_blue"
                  app:tipsTextSize="@dimen/dp15" />


  **java:**

        final ValueConvertor convertorMoney = new ValueConvertor() {
                   @NotNull
                   @Override
                   public String valueToTipText(double value) {
                       return 0;
                   }

                   @Override
                   public double progressToValue(int progress) {
                       return 0;
                   }

                   @Override
                   public int valueToProgress(double value) {
                       return 0;
                   }

               };
               mSbMoney.setLocalValueConvertor(convertorMoney);
               mSbMoney.setLeftText("");
               mSbMoney.setRightText("");
               mSbMoney.setOnDragCallBack(new OnSlideDrag() {
                   @Override
                   public void onDragStart(View v) {

                   }

                   @Override
                   public void onDraging(View v, int percent) {
                        //dosomething
                   }

                   @Override
                   public void onDragEnd(View v, boolean isComplete) {

                   }
               });