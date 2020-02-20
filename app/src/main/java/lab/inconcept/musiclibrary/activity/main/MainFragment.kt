package lab.inconcept.musiclibrary.activity.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.databinding.FragmentMainBinding
import lab.inconcept.musiclibrary.fragment.DetailedMusicFragment

class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel

    companion object {
        fun getInstance(): MainFragment = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setHasOptionsMenu(true)
        mainViewModel.onCreate(this)
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater, R.layout.fragment_main, container, false).apply {
            lifecycleOwner = this@MainFragment
            viewModel = mainViewModel
            adapter = MusicListAdapter {
                val fragment = DetailedMusicFragment.getInstance(it)
                (activity as MainActivity).openFragment(fragment, true)
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mainViewModel.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        mainViewModel.onDestroyView()
        super.onDestroyView()
    }
}