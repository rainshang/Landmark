package com.xyx.landmark.ui.note


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xyx.landmark.BR
import com.xyx.landmark.R
import com.xyx.landmark.databinding.FragmentNoteBinding
import kotlinx.android.synthetic.main.fragment_note.*

class NoteFragment : Fragment() {

    private val args: NoteFragmentArgs by navArgs()
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DataBindingUtil.bind<FragmentNoteBinding>(view)?.apply {
            setVariable(BR.viewModel, this@NoteFragment.viewModel)
            lifecycleOwner = viewLifecycleOwner
        }
        btn.setOnClickListener { viewModel.publishNote(args.loc) }
        btn.isEnabled = false
        viewModel.apply {
            isSuccessful.observe(viewLifecycleOwner, Observer {
                Toast.makeText(
                    context?.applicationContext,
                    getString(R.string.tip_publish_successful, args.loc.lat, args.loc.lng),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            })
            errMsg.observe(
                viewLifecycleOwner,
                Observer {
                    Toast.makeText(context?.applicationContext, it, Toast.LENGTH_SHORT).show()
                })
        }
    }

}
