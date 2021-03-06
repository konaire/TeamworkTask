package com.konaire.masterdetail.ui.projects

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.konaire.masterdetail.R
import com.konaire.masterdetail.models.Project
import com.konaire.masterdetail.presenters.projects.ProjectsListPresenter
import com.konaire.masterdetail.ui.BaseFragment
import com.konaire.masterdetail.ui.BaseListView
import com.konaire.masterdetail.ui.list.DividerDecoration
import com.konaire.masterdetail.ui.projects.adapters.ProjectAdapter

import dagger.android.support.AndroidSupportInjection

import kotlinx.android.synthetic.main.fragment_projects_list.*

import javax.inject.Inject

/**
 * Created by Evgeny Eliseyev on 23/04/2018.
 */
interface ProjectsListView: BaseListView<Project>

class ProjectsListFragment: BaseFragment(), ProjectsListView {
    @Inject lateinit var presenter: ProjectsListPresenter
    private var adapter: ProjectAdapter? = null

    companion object {
        private const val TAG = "PROJECTS_LIST"

        fun create(): ProjectsListFragment {
            val fragment = ProjectsListFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_projects_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (adapter == null) {
            adapter = ProjectAdapter(this)
        }

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(activity)
        list.addItemDecoration(DividerDecoration(activity!!))
        list.setHasFixedSize(true)

        swipe.setOnRefreshListener { presenter.getProjects() }
        emptyView?.visibility = if (adapter!!.isNotEmpty()) View.GONE else View.VISIBLE

        presenter.getProjects()
    }

    override fun onStop() {
        super.onStop()
        presenter.stopSubscriptions()
    }

    override fun getTitle(): String = getString(R.string.app_name)

    override fun getFragmentTag(): String = TAG

    override fun isRoot(): Boolean = true

    override fun showProgress() {
        swipe?.isRefreshing = true
    }

    override fun hideProgress() {
        swipe?.isRefreshing = false
    }

    override fun showData(data: MutableList<Project>) {
        if (data.isNotEmpty()) {
            adapter?.reinit(data)
            emptyView?.visibility = View.GONE
        } else {
            emptyView?.visibility = View.VISIBLE
        }
    }

    override fun onItemClicked(item: Project, view: View?) =
        (activity as ProjectActivity).showFragment(ProjectDetailFragment.create(item))
}