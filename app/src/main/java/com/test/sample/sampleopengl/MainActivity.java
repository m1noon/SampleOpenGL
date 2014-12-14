package com.test.sample.sampleopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MainActivity extends ActionBarActivity implements GLSurfaceView.Renderer{

    private static final String TAG = MainActivity.class.getSimpleName();

    private GLSurfaceView mGlSurfaceView;

    private static final String VSHADER_SOURCE =
            "void main()  {\n" +
            "   gl_Position = vec4(-1.0, 1.0, 0.0, 1.0);\n" +
            "   gl_PointSize = 20.0;\n" +
            "}\n";

    private static final String FSHADER_SOURCE =
            "void main() {\n" +
            " gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
            "}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "setContentView finished.");
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.main_activity$gl_surface_view);
        initGLES20(mGlSurfaceView);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated called.");
        GLES20.glClearColor(0.0f,1.0f,1.0f,1.0f);
        // シェーダー初期化
        initShaders(VSHADER_SOURCE, FSHADER_SOURCE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged called.");
        // 描画領域の設定
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame called.");
        // バッファを前に指定した値でクリア
        // 引数にはカラーバッファ、デプスバッファ、ステンシルバッファを指定出来る。
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 点を描画
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    /**
     * OpenGlESの初期化を行う。<br/>
     *
     * @param glSurfaceView
     */
    private void initGLES20(GLSurfaceView glSurfaceView) {
        // GLESのバージョンの指定
        glSurfaceView.setEGLContextClientVersion(2);
        // Rendererの指定
        glSurfaceView.setRenderer(this);
        // その他細かい指定があれば続けて設定
    }

    /**
     * プログラムオブジェクトを生成し、GLシステムに設定する。
     *
     * @param vSHader 頂点シェーダのプログラム
     * @param fShader フラグメントシェーダのプログラム
     * @return プログラムオブジェクト
     */
    private int initShaders(String vSHader, String fShader) {
        int program = createProgram(vSHader, fShader);

        GLES20.glUseProgram(program);

        return program;
    }

    /**
     * リンク済みプロジェクトオブジェクトを生成する。
     *
     * @param vShader 頂点シェーダのプログラム
     * @param fShader フラグメントシェーダのプログラム
     * @return 作成したプログラムオブジェクト
     */
    private int createProgram(String vShader, String fShader) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vShader);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fShader);

        // プログラムオブジェクトを生成する
        int program = GLES20.glCreateProgram();
        if(program == 0) {
            throw new RuntimeException("failed to create program.");
        }

        // シェーダオブジェクトの設定
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);

        // プログラムオブジェクトをリンクする （プロジェクト内のシェーダオブジェクト感でのリンク）
        GLES20.glLinkProgram(program);

        // リンク結果のチェック
        int[] linked = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0);
        if(linked[0] != GLES20.GL_TRUE) {
            String error = GLES20.glGetProgramInfoLog(program);
            throw new RuntimeException("failed to link program: " + error);
        }
        return program;
    }

    /**
     * シェーダオブジェクトを生成する。
     *
     * @param type シェーダのタイプ
     * @param source シェーダのプログラム(GLSLの文字列)
     * @return 作成したシェーダオブジェクト(のID？)
     */
    private int loadShader(int type, String source) {
        // シェーダオブジェクトの生成
        int shader = GLES20.glCreateShader(type);
        if(shader == 0) {
            throw new RuntimeException("unnable to create shader.");
        }

        // シェーダのプログラムを設定
        GLES20.glShaderSource(shader,source);

        // シェーダをコンパイル
        GLES20.glCompileShader(shader);

        // コンパイル結果の検査
        int[] compile = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
        if(compile[0] != GLES20.GL_TRUE) {
            String error = GLES20.glGetShaderInfoLog(shader);
            throw new RuntimeException("failed to compile shader: " + error);
        }

        return shader;
    }
}
