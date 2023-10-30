package com.example.newsappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newsappkotlin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState
        )

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}