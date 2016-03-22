package com.haokan.xinyitu.login_register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class Login_Register_SwAnim_Manager {
    public static TimeInterpolator sInterpolatorIn = new TimeInterpolator() {
        @Override
        public float getInterpolation(float input) {
            input -= 1.0f;
            return  input * input * ((3.0f) * input +1.0f) + 1.0f;
        }
    };
    private static TimeInterpolator sInterpolatorOut = new DecelerateInterpolator(2.0f);

    private static long sDuation = 300;

//    public static ValueAnimator rightToLeftSw(final int distance, final ArrayList<View> viewsOut, final ArrayList<View> viewsIn) {
//        ValueAnimator anim = ValueAnimator.ofFloat(0, 1.0f);
//        anim.setDuration(sDuation);
//        anim.setInterpolator(sInterpolatorDefault);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                for (View view : viewsIn) {
//                    view.setTranslationX(distance);
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                for (View view : viewsOut) {
//                    view.setVisibility(View.GONE);
//                    view.setTranslationX(0);
//                }
//            }
//        });
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float f = animation.getAnimatedFraction();
//                float outValue = -sInterpolatorOut.getInterpolation(f) * distance;
//                for (View view : viewsOut) {
//                    view.setTranslationX(outValue);
//                }
//
//                Float inValue = distance * (1.0f - sInterpolatorIn.getInterpolation(f));
//                for (View view : viewsIn) {
//                    view.setTranslationX(inValue);
//                }
//                Log.d("wangzixu", "outValue, inValue = " + outValue + ", " + inValue);
//            }
//        });
//        return anim;
//    }

//    public static ValueAnimator leftToRightSw(final int distance, final ArrayList<View> viewsOut, final ArrayList<View> viewsIn) {
//        ValueAnimator anim = ValueAnimator.ofFloat(0, 1.0f);
//        anim.setDuration(sDuation);
//        anim.setInterpolator(sInterpolatorDefault);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                for (View view : viewsIn) {
//                    view.setTranslationX(-distance);
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                for (View view : viewsOut) {
//                    view.setVisibility(View.GONE);
//                    view.setTranslationX(0);
//                }
//            }
//        });
//        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float f = animation.getAnimatedFraction();
//                float outValue = sInterpolatorOut.getInterpolation(f) * distance;
//                for (View view : viewsOut) {
//                    view.setTranslationX(outValue);
//                }
//
//                Float inValue = (sInterpolatorIn.getInterpolation(f) - 1.0f) * distance;
//                for (View view : viewsIn) {
//                    view.setTranslationX(inValue);
//                }
//            }
//        });
//        return anim;
//    }


    public static ValueAnimator rightToLeftIn(final int distance, final View ...views) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, distance);
        anim.setDuration(sDuation);
        anim.setInterpolator(sInterpolatorIn);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                for (View view : views) {
//                    view.setTranslationX(distance);
//                }
//            }
//        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float i = distance - (Float) animation.getAnimatedValue();
                for (View view : views) {
                    view.setTranslationX(i);
                }
            }
        });
        return anim;
    }

    public static ValueAnimator rightToLeftOut(final int distance, final View ...views) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, distance);
        anim.setDuration(sDuation);
        anim.setInterpolator(sInterpolatorOut);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (View view : views) {
                    view.setVisibility(View.GONE);
                    view.setTranslationX(0);
                }
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float i = -(Float) animation.getAnimatedValue();
                for (View view : views) {
                    view.setTranslationX(i);
                }
            }
        });
        return anim;
    }

    public static ValueAnimator leftToRightIn(final int distance, final View ...views) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, distance);
        anim.setDuration(sDuation);
        anim.setInterpolator(sInterpolatorIn);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                for (View view : views) {
//                    view.setTranslationX(-distance);
//                }
//            }
//        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float i = (Float) animation.getAnimatedValue() - distance;
                for (View view : views) {
                    view.setTranslationX(i);
                }
            }
        });
        return anim;
    }

    public static ValueAnimator leftToRightOut(final int distance, final View ...views) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, distance);
        anim.setDuration(sDuation);
        anim.setInterpolator(sInterpolatorOut);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                for (View view : views) {
                    view.setVisibility(View.GONE);
                    view.setTranslationX(0);
                }
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float i = (Float) animation.getAnimatedValue();
                for (View view : views) {
                    view.setTranslationX(i);
                }
            }
        });
        return anim;
    }
}
