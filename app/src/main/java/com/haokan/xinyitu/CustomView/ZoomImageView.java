package com.haokan.xinyitu.CustomView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.haokan.xinyitu.R;

public class ZoomImageView extends ImageView {
	private float mLastX;
	private float mLastY;
	private Matrix mMatrix = new Matrix();
	private float mSavedScale = 1.0f;
	private float mMatrixX, mMatrixY;

	private float mOldDistant;
	private float mCurrentDistant;
	private PointF mCenter = new PointF();

	private float mMinScale = 0.6f;
	private float mMaxScale = 2.0f;

	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public static final int ZOOM_ANIM = 3;
	// �������ź�ص��Ĺ���
	private boolean isZoomAnim = false;

	private float[] mTmpArray = new float[9];
	private static TimeInterpolator sInterpolator = new DecelerateInterpolator();
	private AnimatorUpdateListener zoomBacklistener = null;

	// ����Ϊԭʼbitmap��ߣ� ���ؽ�imagview����Ӧimageview��Ŀ�ߣ�imageview�Ŀ��
	private float mOriBmpWidth = 0, mOriBmpHeight = 0, mBmpWidth = 0,
			mBmpHeight = 0, mWidth, mHeight = 0;
	// bitmap��imageView�У�
	// x��y�����ʣ��Ŀռ䣬��Ϊbitmapֻ��һ��������Ӧimageview����һ�������Ȼ����ʣ��Ŀռ䡣
	// ��imageview�Ŀ�߼�ȥbitmap�Ŀ�ߣ�����ֵΪ��˵��bitmap��imageview���棬ֵΪ��˵��biamp��imageview��
	private float mRedundantXSpace, mRedundantYSpace;
	private int mMode;
	private boolean mAlreadyLoadBigBmp = false;
	private boolean mCanZoom = true;
	private boolean mIsOnLeftSide = false, mIsOnRightSide = false;
	private int mPointCount;

	private Bitmap mBitmap = null;
	private boolean mParentCanScroll = false;

    /**
     * 图片截图方框，比如用在截图时，图片在滑动时，或者缩放时，图片的边不能进入截图框里面，
     * 这个方框就是这里的截图框，因为有剪裁框时剪裁框总是比较小的那个，所以许多的条件检查都是用的剪裁框，
     * 所以如果没有剪裁框时应该使这个变量等于mEdgeRect
     */
    private RectF mClipRect;

    /**
     * 图片初次显示时，会根据一个区域来填充bitmap，比如系统默认的就是此view的宽高减去padding值，
     * 而我们是自己用matrix填充的bitmap，所以要有一个填充图片的区域，即边缘Rect
     */
    private RectF mEdgeRect;
    /**
     * 图片的填充是自己实现的，1，模拟系统的center_crop.其他,模拟系统的fitCenter
     */
    private  int mScaleType = 1;

    /**
     * 是否支持剪裁功能
     */
    private boolean mHasClip = false;

	public ZoomImageView(Context context) {
		super(context);
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZoomImageView, defStyleAttr, 0);

        mHasClip = a.getBoolean(R.styleable.ZoomImageView_ziv_hasClip, false);
        mScaleType = a.getInt(R.styleable.ZoomImageView_ziv_scaleType, 1);
        a.recycle();
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

    /**
     * 自己实现初始时图片怎么显示，这里是用matrix实现的center_crop的效果，
     * 而且可以实现以任意的框来center_crop, 系统的scaleType除了fitXy，其他
     * 的也是这样用matrix变换的，参考系统源码，imageview中的configureBounds()方法
     */
	private void calcBitmapWH() {
		if (mWidth == 0 || mHeight == 0 || mOriBmpWidth == 0
				|| mOriBmpHeight == 0) {
			return;
		}
		if (mEdgeRect == null) {
            mEdgeRect = new RectF(0, 0, mWidth, mHeight);
            if (!mHasClip) {
                mClipRect = mEdgeRect;
            }
		}

        //模拟系统的形式来处理图片，参考系统源码，imageview中的configureBounds()方法
        float scale = 1.0f;
		if (mScaleType == 1) { //center_crop
            float dx = 0, dy = 0;
            if (mOriBmpWidth * mEdgeRect.height() > mEdgeRect.width() * mOriBmpHeight) {
                scale = mEdgeRect.height() / mOriBmpHeight;
                dx = mEdgeRect.left + (mEdgeRect.width() - mOriBmpWidth * scale) * 0.5f;
            } else {
                scale = mEdgeRect.width() / mOriBmpWidth;
                dy = mEdgeRect.top + (mEdgeRect.height() - mOriBmpHeight * scale) * 0.5f;
            }
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(Math.round(dx), Math.round(dy));
            setImageMatrix(mMatrix);
        } else  {//fitCenter
            //// TODO: 2016/3/8 模拟系统的fitcenter
        }

        // 本来bitmap的宽高是不规则的，初始化后scale参数也是一个不规则的数字，为了后面计算的方便，
        // 我们可以规定初始化后的scale为1.0，bitmap宽高为初始化后的宽高，计算缩放等是用的matrix.postScale
        // 是一个缩放的增量系数
        mSavedScale = 1.0f;
        mBmpWidth = scale * mOriBmpWidth;
        mBmpHeight = scale * mOriBmpHeight;
        calcRedundantSpace();

//		mMatrix.setScale(scale, scale);
//		mSavedScale = 1.0f;
//		mBmpWidth = scale * mOriBmpWidth;
//		mBmpHeight = scale * mOriBmpHeight;
//
//		calcRedundantSpace();
//		mMatrix.postTranslate(mClipRect.left + mRedundantXSpace / 2.0f,
//				mClipRect.top + mRedundantYSpace / 2.0f);
//		setImageMatrix(mMatrix);
	}

	public void setScaleMode(int mode) {
		mScaleType = mode;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = getWidth();
		mHeight = getHeight();
		calcBitmapWH();
		super.onSizeChanged(w, h, oldw, oldh);
	}

    private void setup() {
        if (mBitmap != null) {
            mOriBmpWidth = mBitmap.getWidth();
            mOriBmpHeight = mBitmap.getHeight();
            mBmpWidth = 0;
            mBmpHeight = 0;
            calcBitmapWH();
            Log.d("wangzixu", "setup w h = " + mOriBmpWidth + ", " + mOriBmpHeight);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public void setParentCanScroll(boolean can) {
		mParentCanScroll = can;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (!mParentCanScroll) {
			hanleTouchEvent(event, true);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	public int hanleTouchEvent(MotionEvent event, boolean parentIsOnEdge) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mMode = NONE;
			mPointCount = 1;
//			if (mParentCanScroll && mSavedScale > 1.0f && parentIsOnEdge) {
//				mMode = DRAG;
//			} else if (mSavedScale >= 1.0f && parentIsOnEdge) {
//				mMode = DRAG;
//			} else if (mHasClip) {
//            }
            mMode = DRAG;
			mLastX = event.getX(0);
			mLastY = event.getY(0);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mPointCount++;
			if (!mCanZoom || mPointCount > 2) {
				break;
			}
			mOldDistant = getDistance(event);
			if (mOldDistant > 10f) {
				mMode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mPointCount--;
			if (mPointCount > 1) {
				break;
			}
			mMode = NONE;
			if (mSavedScale > 1.0f) {
				mMode = DRAG;
				int pointerIndex = event.getActionIndex();
				mLastX = event.getX(1 - pointerIndex);
				mLastY = event.getY(1 - pointerIndex);
			} else if (mSavedScale < 1.0f && !isZoomAnim && !mHasClip && mMode != ZOOM_ANIM) {
				isZoomAnim = true;
				mMode = ZOOM_ANIM;
				startBackAnim();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMode == DRAG) {
				float currentX = event.getX(0);
				float currentY = event.getY(0);
				float deltaX = currentX - mLastX;
				float deltaY = currentY - mLastY;
				mLastX = currentX;
				mLastY = currentY;
				if (mParentCanScroll) {
					if (mRedundantXSpace >= 0) {
						if (Math.abs(deltaX) > Math.abs(deltaY)
								&& Math.abs(deltaX) > 20) {
							event.setAction(MotionEvent.ACTION_DOWN);
							mMode = NONE;
							break;
						}
					} else if ((Math.abs(deltaX) > Math.abs(deltaY))
							&& ((mIsOnLeftSide && deltaX > 0) || (mIsOnRightSide && deltaX < 0))) {
						event.setAction(MotionEvent.ACTION_DOWN);
						mMode = NONE;
						break;
					}
				}
				checkAndSetTranslate(deltaX, deltaY);
			} else if (mMode == ZOOM) {
				mCurrentDistant = getDistance(event);
				float scaleFactor = mCurrentDistant / mOldDistant;
				mOldDistant = mCurrentDistant;
				float deltaScale = Math.abs(scaleFactor - 1.0f);
				if (deltaScale < 0.001) {
					break;
				}
				if (scaleFactor > 1.05f) {
					scaleFactor = 1.05f;
				} else if (scaleFactor < 0.95f) {
					scaleFactor = 0.95f;
				}
				getCenter(mCenter, event);
				zoomImg(scaleFactor);
			} else if (mMode == ZOOM_ANIM) {
				event.setAction(MotionEvent.ACTION_DOWN);
				mMode = NONE;
			}
			break;
		case MotionEvent.ACTION_UP:
			mMode = NONE;
			mPointCount = 0;
			if (mSavedScale > 1.0f && !mIsOnLeftSide && !mIsOnRightSide) {
				mMode = DRAG;
			}
			break;
		}
		return mMode;
	}

	public void checkIsOnSide() {
		getMatrixXY(mMatrix);
		mIsOnLeftSide = false;
		mIsOnRightSide = false;
		if (-(mMatrixX - mClipRect.left) <= 3f) {
			mIsOnLeftSide = true;
		} else if (mMatrixX - mClipRect.left <= mRedundantXSpace + 3f) {
			mIsOnRightSide = true;
		}
	}

	private void startBackAnim() {
		ValueAnimator anim = ValueAnimator.ofFloat(mSavedScale, 1.0f);
		anim.setDuration(200);
		anim.setInterpolator(sInterpolator);
		if (zoomBacklistener == null) {
			zoomBacklistener = new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float f = (Float) animation.getAnimatedValue();
					float scaleFactor = f / mSavedScale;
					zoomImg(scaleFactor);
				}
			};
		}
		anim.addUpdateListener(zoomBacklistener);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				isZoomAnim = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				isZoomAnim = false;
			}
		});
		anim.start();
	}

    /**
     * 检查并且重新设置缩放系数，防止越过一些边缘极限值
     * 例如超过了最大，最小缩放值，支持剪裁的情况下缩放到剪裁框大小。
     */
    private float checkAndSetScaleFactor(float scaleFactor) {
        float origScale = mSavedScale;
        mSavedScale *= scaleFactor;
        if (mSavedScale > mMaxScale) {
            mSavedScale = mMaxScale;
            scaleFactor = mMaxScale / origScale;
        } else if (mSavedScale < mMinScale) {
            mSavedScale = mMinScale;
            scaleFactor = mMinScale / origScale;
        }
        if (scaleFactor == 1.0f) {
            return 1.0f;
        }
        if (mHasClip) {
            calcRedundantSpace();
            if (mRedundantXSpace > mRedundantYSpace && mRedundantXSpace >= 0) {
                mSavedScale = mClipRect.width() / mBmpWidth;
                scaleFactor = mSavedScale / origScale;
            } else if (mRedundantXSpace < mRedundantYSpace && mRedundantYSpace >= 0) {
                mSavedScale = mClipRect.height() / mBmpHeight;
                scaleFactor = mSavedScale / origScale;
            }
            calcRedundantSpace();
        }
        return scaleFactor;
    }

	private void zoomImg(float f) {
        float scaleFactor = checkAndSetScaleFactor(f);
		if (scaleFactor == 1.0f) {
			return;
		}

		float px, py;
		px = mRedundantXSpace >= 0 ? mClipRect.centerX() : mCenter.x;
		py = mRedundantYSpace >= 0 ? mClipRect.centerY() : mCenter.y;
		mMatrix.postScale(scaleFactor, scaleFactor, px, py);
		getMatrixXY(mMatrix);

		if (scaleFactor < 1.0f) {
			if (mRedundantXSpace >= 0) {
				if (mMatrixX != mClipRect.left + mRedundantXSpace / 2.0f) {
					mMatrix.postTranslate(mClipRect.left + mRedundantXSpace
                            / 2.0f - mMatrixX, 0);
                }
            } else if (scaleFactor < 1.0f) {
				if (mMatrixX - mClipRect.left > 0) {
					mMatrix.postTranslate(mClipRect.left - mMatrixX, 0);
				} else if (mMatrixX - mClipRect.left < mRedundantXSpace) {
					mMatrix.postTranslate(mClipRect.left + mRedundantXSpace
							- mMatrixX, 0);
				}
			}

			if (mRedundantYSpace >= 0) {
				if (mMatrixY != mClipRect.top + mRedundantYSpace / 2.0f) {
					mMatrix.postTranslate(0, mClipRect.top
							+ mRedundantYSpace / 2.0f - mMatrixY);
				}
			} else if (scaleFactor < 1.0f) {
				if (mMatrixY - mClipRect.top > 0) {
					mMatrix.postTranslate(0, mClipRect.top - mMatrixY);
				} else if (mMatrixY - mClipRect.top < mRedundantYSpace) {
					mMatrix.postTranslate(0, mClipRect.top
							+ mRedundantYSpace - mMatrixY);
				}
			}
		}
		setImageMatrix(mMatrix);
	}

	private float getDistance(MotionEvent event) {
		float x = event.getX(1) - event.getX(0);
		float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt((x * x + y * y));
	}

	private PointF getCenter(PointF centerF, MotionEvent event) {
		float x = (event.getX(1) + event.getX(0)) / 2;
		float y = (event.getY(1) + event.getY(0)) / 2;
		centerF.set(x, y);
		return centerF;
	}

	public boolean isAlreadyLoadBigBmp() {
		return mAlreadyLoadBigBmp;
	}

	public void setAlreadyLoadBigBmp(boolean alreadyLoadBigBmp) {
		mAlreadyLoadBigBmp = alreadyLoadBigBmp;
	}

    /**
     * 因为图片边缘不能进入剪裁框，所以需要知道剪裁框和图片宽高之间的差值，
     * 即x，y方向的冗余量，负数说明图片大于剪裁框
     */
	private void calcRedundantSpace() {
        mRedundantXSpace = mClipRect.width() - mSavedScale * mBmpWidth;
        mRedundantYSpace = mClipRect.height() - mSavedScale * mBmpHeight;
	}

	private void checkAndSetTranslate(float deltaX, float deltaY) {
		getMatrixXY(mMatrix);
		if (mRedundantXSpace >= 0) {
			deltaX = 0;
		} else {
			if (mMatrixX - mClipRect.left + deltaX > 0) {
				deltaX = mClipRect.left - mMatrixX;
			} else if (mMatrixX - mClipRect.left + deltaX < mRedundantXSpace) {
				deltaX = mClipRect.left + mRedundantXSpace - mMatrixX;
			}
		}

		if (mRedundantYSpace >= 0) {
			deltaY = 0;
		} else {
			if (mMatrixY - mClipRect.top + deltaY > 0) {
				deltaY = mClipRect.top - mMatrixY;
			} else if (mMatrixY - mClipRect.top + deltaY < mRedundantYSpace) {
				deltaY = mClipRect.top + mRedundantYSpace - mMatrixY;
			}
		}
		mMatrix.postTranslate(deltaX, deltaY);
		setImageMatrix(mMatrix);
		checkIsOnSide();
	}

	/**
	 * ��ȡmatrix�е�λ������������mMatrixX��Y��
	 */
	private void getMatrixXY(Matrix m) {
		m.getValues(mTmpArray);
		mMatrixX = mTmpArray[Matrix.MTRANS_X];
		mMatrixY = mTmpArray[Matrix.MTRANS_Y];
	}

	public boolean canParentScroll(float deltaX) {
		if (mMode == ZOOM) {
			return false;
		}
		if (mSavedScale <= 1.0f) {
			return true;
		}
		if (mRedundantXSpace >= 0) {
			return true;
		}
		if ((mIsOnLeftSide && deltaX > 0) || (mIsOnRightSide && deltaX < 0)) {
			return true;
		}
		return false;
	}

	public float getReDundantXSpace() {
		return mRedundantXSpace;
	}

	public void setCanZoom(boolean canZoom) {
		mCanZoom = canZoom;
	}

	public int getTouchMode() {
		return mMode;
	}

	public boolean canHorizontalDrag() {
		if (mSavedScale > 1.0f && mRedundantXSpace < 0) {
			return true;
		}
		return false;
	}

	public float getSavedScale() {
		return mSavedScale;
	}

	public boolean getIsOnLeftSide() {
		return mIsOnLeftSide;
	}

	public boolean getIsOnRightSide() {
		return mIsOnRightSide;
	}

	public void setClipRect(RectF rect) {
		mClipRect = rect;
	}

	public RectF getClipRect() {
		return mClipRect;
	}

	public void setMaxMinScale(float max, float min) {
		mMaxScale = max;
		mMinScale = min;
	}

	public Bitmap getOriginalBmp() {
		return mBitmap;
	}

	public Matrix getmMatrix() {
		return mMatrix;
	}
}
