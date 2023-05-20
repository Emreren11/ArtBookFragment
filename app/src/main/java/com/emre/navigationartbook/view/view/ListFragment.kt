package com.emre.navigationartbook.view.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.emre.navigationartbook.databinding.FragmentListBinding
import com.emre.navigationartbook.view.adapter.ArtAdapter
import com.emre.navigationartbook.view.model.Arts
import com.emre.navigationartbook.view.roomdb.ArtDB
import com.emre.navigationartbook.view.roomdb.ArtDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var db: ArtDB
    private lateinit var artDao: ArtDao
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = MainActivity()

        db = Room.databaseBuilder(requireContext(),ArtDB::class.java,"Arts").build()
        artDao = db.artDao()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getList()
    }

    private fun getList() {
        compositeDisposable.add(
            artDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(artlist: List<Arts>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = ArtAdapter(artlist)
    }


}