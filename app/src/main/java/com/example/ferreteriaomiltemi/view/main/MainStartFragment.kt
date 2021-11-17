package com.example.ferreteriaomiltemi.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ferreteriaomiltemi.R
import com.example.ferreteriaomiltemi.databinding.FragmentLoginBinding
import com.example.ferreteriaomiltemi.databinding.FragmentMainStartBinding
import com.example.ferreteriaomiltemi.view.products.ProductsActivity
import com.synnapps.carouselview.ImageListener

class MainStartFragment : Fragment() {
    private var _binding: FragmentMainStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentMainStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagesCarousel = arrayOf(R.drawable.banner_carousel1,
                                    R.drawable.banner_carousel2,
                                    R.drawable.banner_carousel3)
        var imageListener = ImageListener { position, imageView ->
            imageView?.setImageResource(imagesCarousel[position])
        }
        // Events
        binding.carouselView.setImageListener(imageListener)
        binding.carouselView.pageCount = imagesCarousel.size
        binding.imvMainCategory.setOnClickListener { goProducts("category") }
        binding.imvMainBrand.setOnClickListener { goProducts("brand") }
        binding.imvMainDiscounts.setOnClickListener { goProducts("discounts") }
    }

    fun goProducts(type: String){
        var intent = Intent(activity, ProductsActivity::class.java)
        intent.putExtra("filter", type)
        startActivity(intent)
    }
}