package com.task.joynt.DotsLoader

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.task.joynt.R
import java.util.*
import kotlin.collections.ArrayList

class DotsLoader : View{
    private var diffRadius: Int = 0
    lateinit var dotsXCorArr: FloatArray
    private var defaultCirclePaint: Paint? = null
    private var selectedCirclePaint: ArrayList<Paint>? = null
    private var selectedDotPos = 1
    private var timer: Timer? = null
    var animDur = 500

    constructor(context: Context) : super(context) {
        initCordinates()
        initPaints()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        initAttributes(attrs)
        initCordinates()
        initPaints()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        initAttributes(attrs)
        initCordinates()
        initPaints()
    }

    private fun initAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotsLoader, 0, 0)

        this.selRadius = typedArray.getDimensionPixelSize(R.styleable.DotsLoader_loader_selectedRadius, radius + 10)

        this.dotsDistance = typedArray.getDimensionPixelSize(R.styleable.DotsLoader_loader_dotsDist, 15)

        typedArray.recycle()
    }

    fun initCordinates() {
        diffRadius = this.selRadius - radius

        dotsXCorArr = FloatArray(this.noOfDots)

        for (i in 0 until noOfDots) {
            dotsXCorArr[i] = (i * dotsDistance + (i * 2 + 1) * radius).toFloat()
        }
    }
    fun initPaints() {
        defaultCirclePaint = Paint()
        defaultCirclePaint?.isAntiAlias = true
        defaultCirclePaint?.style = Paint.Style.FILL
        defaultCirclePaint?.color = defaultColor

        selectedCirclePaint = ArrayList()

        for (i in 0 until selectedColor.size)
        {
            selectedCirclePaint!!.add(Paint())
            selectedCirclePaint!![i].isAntiAlias = true
            selectedCirclePaint!![i].style = Paint.Style.FILL
            selectedCirclePaint!![i].color = selectedColor.get(i)
        }

    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width: Int=(2 * this.noOfDots * radius + (this.noOfDots - 1) * dotsDistance + 2 * diffRadius)
        val height: Int= 2 * this.selRadius

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawView(canvas)
    }
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != VISIBLE) {
            timer?.cancel()
        } else {
            scheduleTimer()
        }
    }
    private fun scheduleTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                selectedDotPos++
                if (selectedDotPos > noOfDots) {

                    selectedDotPos = 1
                }
                (Utils.scanForActivity(context))?.runOnUiThread { invalidate() }
            }
        }, 0, animDur.toLong())
    }

    private fun drawView(canvas: Canvas) {
        for (i in 0 until noOfDots){
            var xCor = dotsXCorArr[i]
            if (i + 1 == selectedDotPos) {
                xCor += diffRadius.toFloat()
            } else if (i + 1 > selectedDotPos) {
                xCor += (2 * diffRadius).toFloat()
            }

            if (i + 1 == selectedDotPos) {
                canvas.drawCircle(
                    xCor,
                    this.selRadius.toFloat(),
                    this.selRadius.toFloat(),
                    selectedCirclePaint?.get(i)!!)
            }  else {
                canvas.drawCircle(
                    xCor,
                    this.selRadius.toFloat(),
                    radius.toFloat(),
                    defaultCirclePaint!!)
            }
        }
    }


    var dotsDistance: Int = 15
        set(value) {
            field = value
            initCordinates()
        }

    var selRadius: Int = 38
        set(selRadius) {
            field = selRadius
            initCordinates()
        }

    var radius: Int = 30
        set(radius) {
            field = radius
            initCordinates()
        }
    var noOfDots: Int = 5
        set(noOfDots) {
            field = noOfDots
            initCordinates()
        }
    var defaultColor: Int = resources.getColor(android.R.color.darker_gray)
        set(defaultColor) {
            field = defaultColor
            defaultCirclePaint?.color = defaultColor
        }

    var selectedColor: Array<Int> = arrayOf(resources.getColor(R.color.blue),resources.getColor(R.color.green),
        resources.getColor(R.color.yellow),resources.getColor(R.color.orange),resources.getColor(R.color.red))
        set(selectedColor) {
            field = selectedColor
            selectedCirclePaint?.let {
                for (i in 0 until selectedColor.size)
                {
                    it[i].color = selectedColor.get(i)
                }
            }
        }

}