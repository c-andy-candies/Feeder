package com.nononsenseapps.feeder.ui.compose.feedarticle

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.nononsenseapps.feeder.ApplicationCoroutineScope
import com.nononsenseapps.feeder.R
import com.nononsenseapps.feeder.archmodel.TextToDisplay
import com.nononsenseapps.feeder.blob.blobFile
import com.nononsenseapps.feeder.blob.blobFullFile
import com.nononsenseapps.feeder.blob.blobFullInputStream
import com.nononsenseapps.feeder.blob.blobInputStream
import com.nononsenseapps.feeder.db.room.ID_ALL_FEEDS
import com.nononsenseapps.feeder.db.room.ID_UNSET
import com.nononsenseapps.feeder.model.LocaleOverride
import com.nononsenseapps.feeder.model.opml.exportOpml
import com.nononsenseapps.feeder.model.opml.importOpml
import com.nononsenseapps.feeder.ui.compose.feed.FeedGridContent
import com.nononsenseapps.feeder.ui.compose.feed.FeedListContent
import com.nononsenseapps.feeder.ui.compose.feed.FeedListItem
import com.nononsenseapps.feeder.ui.compose.feed.FeedScreen
import com.nononsenseapps.feeder.ui.compose.icons.CustomFilled
import com.nononsenseapps.feeder.ui.compose.icons.TextToSpeech
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerFeed
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerItemWithUnreadCount
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerTag
import com.nononsenseapps.feeder.ui.compose.navdrawer.DrawerTop
import com.nononsenseapps.feeder.ui.compose.navdrawer.ListOfFeedsAndTags
import com.nononsenseapps.feeder.ui.compose.navigation.EditFeedDestination
import com.nononsenseapps.feeder.ui.compose.navigation.SearchFeedDestination
import com.nononsenseapps.feeder.ui.compose.navigation.SettingsDestination
import com.nononsenseapps.feeder.ui.compose.readaloud.HideableTTSPlayer
import com.nononsenseapps.feeder.ui.compose.reader.ReaderView
import com.nononsenseapps.feeder.ui.compose.reader.dateTimeFormat
import com.nononsenseapps.feeder.ui.compose.reader.onLinkClick
import com.nononsenseapps.feeder.ui.compose.text.htmlFormattedText
import com.nononsenseapps.feeder.ui.compose.theme.DynamicTopAppBar
import com.nononsenseapps.feeder.ui.compose.theme.SetStatusBarColorToMatchScrollableTopAppBar
import com.nononsenseapps.feeder.ui.compose.theme.dynamicScrollBehavior
import com.nononsenseapps.feeder.ui.compose.theme.isLight
import com.nononsenseapps.feeder.ui.compose.utils.ImmutableHolder
import com.nononsenseapps.feeder.ui.compose.utils.LocalWindowSize
import com.nononsenseapps.feeder.ui.compose.utils.ScreenType
import com.nononsenseapps.feeder.ui.compose.utils.WindowSize
import com.nononsenseapps.feeder.ui.compose.utils.getScreenType
import com.nononsenseapps.feeder.util.openGitlabIssues
import com.nononsenseapps.feeder.util.openLinkInBrowser
import com.nononsenseapps.feeder.util.openLinkInCustomTab
import com.nononsenseapps.feeder.util.unicodeWrap
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.kodein.di.compose.LocalDI
import org.kodein.di.instance
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FeedArticleScreen(
    navController: NavController,
    viewModel: FeedArticleViewModel,
) {
    val viewState: FeedArticleScreenViewState by viewModel.viewState.collectAsState()
    val pagedFeedItems = viewModel.currentFeedListItems.collectAsLazyPagingItems()

    val di = LocalDI.current
    val opmlExporter = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/xml")
    ) { uri ->
        if (uri != null) {
            val applicationCoroutineScope: ApplicationCoroutineScope by di.instance()
            applicationCoroutineScope.launch {
                exportOpml(di, uri)
            }
        }
    }
    val opmlImporter = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val applicationCoroutineScope: ApplicationCoroutineScope by di.instance()
            applicationCoroutineScope.launch {
                importOpml(di, uri)
            }
        }
    }

    val feedArticleScreenType = getFeedArticleScreenType(
        windowSize = LocalWindowSize(),
        viewState = viewState,
    )

    val context = LocalContext.current

    // Each feed gets its own scroll state. Persists across device rotations, but is cleared when
    // switching feeds
    val feedListState = key(viewState.currentFeedOrTag) {
        rememberLazyListState()
    }
    val feedGridState = key(viewState.currentFeedOrTag) {
        rememberLazyStaggeredGridState()
    }
    // Each article gets its own scroll state. Persists across device rotations, but is cleared
    // when switching articles.
    val articleListState = key(viewState.articleId) {
        rememberLazyListState()
    }

    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    FeedArticleScreen(
        feedArticleScreenType = feedArticleScreenType,
        viewState = viewState,
        onRefreshVisible = {
            viewModel.requestImmediateSyncOfCurrentFeedOrTag()
        },
        onRefreshAll = {
            viewModel.requestImmediateSyncOfAll()
        },
        onToggleOnlyUnread = { value ->
            viewModel.setShowOnlyUnread(value)
        },
        onToggleOnlyBookmarked = { value ->
            viewModel.setShowOnlyBookmarked(value)
        },
        onDrawerItemSelected = { feedId, tag ->
            viewModel.setCurrentFeedAndTag(feedId, tag)
        },
        onMarkAllAsRead = {
            viewModel.markAllAsRead()
        },
        onToggleTagExpansion = { tag ->
            viewModel.toggleTagExpansion(tag)
        },
        onShowToolbarMenu = { visible ->
            viewModel.setToolbarMenuVisible(visible)
        },
        ttsOnPlay = viewModel::ttsPlay,
        ttsOnPause = viewModel::ttsPause,
        ttsOnStop = viewModel::ttsStop,
        ttsOnSkipNext = viewModel::ttsSkipNext,
        ttsOnSelectLanguage = viewModel::ttsOnSelectLanguage,
        onAddFeed = { SearchFeedDestination.navigate(navController) },
        onEditFeed = { feedId ->
            EditFeedDestination.navigate(navController, feedId)
        },
        onShowEditDialog = {
            viewModel.setShowEditDialog(true)
        },
        onDismissEditDialog = {
            viewModel.setShowEditDialog(false)
        },
        onDeleteFeeds = { feedIds ->
            viewModel.deleteFeeds(feedIds.toList())
        },
        onShowDeleteDialog = {
            viewModel.setShowDeleteDialog(true)
        },
        onDismissDeleteDialog = {
            viewModel.setShowDeleteDialog(false)
        },
        onSettings = {
            SettingsDestination.navigate(navController)
        },
        onSendFeedback = {
            context.startActivity(openGitlabIssues())
        },
        onImport = { opmlImporter.launch(arrayOf("text/plain", "text/xml", "text/opml", "*/*")) },
        onExport = { opmlExporter.launch("feeder-export-${LocalDateTime.now()}.opml") },
        markAsUnread = { itemId, unread ->
            if (unread) {
                viewModel.markAsUnread(itemId)
            } else {
                viewModel.markAsRead(itemId)
            }
        },
        markBeforeAsRead = { index ->
            viewModel.markBeforeAsRead(index)
        },
        markAfterAsRead = { index ->
            viewModel.markAfterAsRead(index)
        },
        onOpenFeedItem = { itemId ->
            viewModel.openArticle(
                itemId = itemId,
                openInBrowser = { articleLink ->
                    openLinkInBrowser(context, articleLink)
                },
                openInCustomTab = { articleLink ->
                    openLinkInCustomTab(context, articleLink, toolbarColor)
                }
            )
        },
        onInteractWithArticle = {
            viewModel.setArticleOpen(true)
        },
        onToggleFullText = {
            if (viewState.textToDisplay == TextToDisplay.FULLTEXT) {
                viewModel.displayArticleText()
            } else {
                viewModel.displayFullText()
            }
        },
        displayFullText = viewModel::displayFullText,
        onMarkAsUnread = {
            viewModel.markAsUnread(viewState.articleId)
        },
        onShareArticle = {
            if (viewState.articleId > ID_UNSET) {
                val intent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        if (viewState.articleLink != null) {
                            putExtra(Intent.EXTRA_TEXT, viewState.articleLink)
                        }
                        putExtra(Intent.EXTRA_TITLE, viewState.articleTitle)
                        type = "text/plain"
                    },
                    null
                )
                context.startActivity(intent)
            }
        },
        onOpenInCustomTab = {
            viewState.articleLink?.let { link ->
                openLinkInCustomTab(context, link, toolbarColor)
            }
        },
        onFeedTitleClick = {
            viewModel.setCurrentFeedAndTag(
                viewState.articleFeedId,
                ""
            )
            viewModel.setArticleOpen(false)
        },
        onNavigateUpFromArticle = {
            viewModel.setArticleOpen(false)
        },
        onToggleCurrentArticlePinned = {
            viewModel.setPinned(viewState.articleId, !viewState.isPinned)
        },
        onSetPinned = { itemId, value ->
            viewModel.setPinned(itemId, value)
        },
        onToggleCurrentArticleBookmarked = {
            viewModel.setBookmarked(viewState.articleId, !viewState.isBookmarked)
        },
        onSetBookmarked = { itemId, value ->
            viewModel.setBookmarked(itemId, value)
        },
        feedListState = feedListState,
        feedGridState = feedGridState,
        articleListState = articleListState,
        pagedFeedItems = pagedFeedItems,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FeedArticleScreen(
    feedArticleScreenType: FeedArticleScreenType,
    viewState: FeedArticleScreenViewState,
    onRefreshVisible: () -> Unit,
    onRefreshAll: () -> Unit,
    onToggleOnlyUnread: (Boolean) -> Unit,
    onToggleOnlyBookmarked: (Boolean) -> Unit,
    onDrawerItemSelected: (Long, String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onToggleTagExpansion: (String) -> Unit,
    onShowToolbarMenu: (Boolean) -> Unit,
    ttsOnPlay: () -> Unit,
    ttsOnPause: () -> Unit,
    ttsOnStop: () -> Unit,
    ttsOnSkipNext: () -> Unit,
    ttsOnSelectLanguage: (LocaleOverride) -> Unit,
    onAddFeed: () -> Unit,
    onEditFeed: (Long) -> Unit,
    onShowEditDialog: () -> Unit,
    onDismissEditDialog: () -> Unit,
    onDeleteFeeds: (Iterable<Long>) -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onSettings: () -> Unit,
    onSendFeedback: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    markAsUnread: (Long, Boolean) -> Unit,
    markBeforeAsRead: (Int) -> Unit,
    markAfterAsRead: (Int) -> Unit,
    onOpenFeedItem: (Long) -> Unit,
    onInteractWithArticle: () -> Unit,
    onToggleFullText: () -> Unit,
    displayFullText: () -> Unit,
    onMarkAsUnread: () -> Unit,
    onShareArticle: () -> Unit,
    onOpenInCustomTab: () -> Unit,
    onFeedTitleClick: () -> Unit,
    onNavigateUpFromArticle: () -> Unit,
    onToggleCurrentArticlePinned: () -> Unit,
    onSetPinned: (Long, Boolean) -> Unit,
    onToggleCurrentArticleBookmarked: () -> Unit,
    onSetBookmarked: (Long, Boolean) -> Unit,
    feedListState: LazyListState,
    feedGridState: LazyStaggeredGridState,
    articleListState: LazyListState,
    pagedFeedItems: LazyPagingItems<FeedListItem>,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    AnimatedVisibility(
        visible = feedArticleScreenType == FeedArticleScreenType.Feed || feedArticleScreenType == FeedArticleScreenType.FeedGrid,
        enter = slideInHorizontally(tween(256), initialOffsetX = { -it }),
        exit = slideOutHorizontally(tween(256), targetOffsetX = { -it }),
    ) {
        ScreenWithNavDrawer(
            drawerState = drawerState,
            feedsAndTags = ImmutableHolder(viewState.drawerItemsWithUnreadCounts),
            expandedTags = ImmutableHolder(viewState.expandedTags),
            onToggleTagExpansion = onToggleTagExpansion,
            onDrawerItemSelected = onDrawerItemSelected,
            content = {
                FeedListScreen(
                    viewState = viewState,
                    onRefreshVisible = onRefreshVisible,
                    onRefreshAll = onRefreshAll,
                    onToggleOnlyUnread = onToggleOnlyUnread,
                    onToggleOnlyBookmarked = onToggleOnlyBookmarked,
                    onMarkAllAsRead = onMarkAllAsRead,
                    onShowToolbarMenu = onShowToolbarMenu,
                    ttsOnPlay = ttsOnPlay,
                    ttsOnPause = ttsOnPause,
                    ttsOnStop = ttsOnStop,
                    ttsOnSkipNext = ttsOnSkipNext,
                    ttsOnSelectLanguage = ttsOnSelectLanguage,
                    onAddFeed = onAddFeed,
                    onEditFeed = onEditFeed,
                    onShowEditDialog = onShowEditDialog,
                    onDismissEditDialog = onDismissEditDialog,
                    onDeleteFeeds = onDeleteFeeds,
                    onShowDeleteDialog = onShowDeleteDialog,
                    onDismissDeleteDialog = onDismissDeleteDialog,
                    onSettings = onSettings,
                    onSendFeedback = onSendFeedback,
                    onImport = onImport,
                    onExport = onExport,
                    drawerState = drawerState,
                    markAsUnread = markAsUnread,
                    markBeforeAsRead = markBeforeAsRead,
                    markAfterAsRead = markAfterAsRead,
                    onOpenFeedItem = onOpenFeedItem,
                    onSetPinned = onSetPinned,
                    onSetBookmarked = onSetBookmarked,
                    feedListState = feedListState,
                    feedGridState = feedGridState,
                    pagedFeedItems = pagedFeedItems,
                    feedArticleScreenType = feedArticleScreenType,
                )
            }
        )
    }

    AnimatedVisibility(
        visible = feedArticleScreenType == FeedArticleScreenType.ArticleDetails,
        enter = slideInHorizontally(tween(256), initialOffsetX = { it }),
        exit = slideOutHorizontally(tween(256), targetOffsetX = { it }),
    ) {
        ArticleScreen(
            viewState = viewState,
            onToggleFullText = onToggleFullText,
            onMarkAsUnread = onMarkAsUnread,
            onShare = onShareArticle,
            onOpenInCustomTab = onOpenInCustomTab,
            onFeedTitleClick = onFeedTitleClick,
            onShowToolbarMenu = onShowToolbarMenu,
            onInteractWithArticle = onInteractWithArticle,
            displayFullText = displayFullText,
            ttsOnPlay = ttsOnPlay,
            ttsOnPause = ttsOnPause,
            ttsOnStop = ttsOnStop,
            ttsOnSkipNext = ttsOnSkipNext,
            ttsOnSelectLanguage = ttsOnSelectLanguage,
            onTogglePinned = onToggleCurrentArticlePinned,
            onToggleBookmarked = onToggleCurrentArticleBookmarked,
            articleListState = articleListState,
            onNavigateUp = onNavigateUpFromArticle,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScreenWithNavDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    feedsAndTags: ImmutableHolder<List<DrawerItemWithUnreadCount>>,
    expandedTags: ImmutableHolder<Set<String>>,
    onToggleTagExpansion: (String) -> Unit,
    onDrawerItemSelected: (Long, String) -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet {
                ListOfFeedsAndTags(
                    feedsAndTags = feedsAndTags,
                    expandedTags = expandedTags,
                    onToggleTagExpansion = onToggleTagExpansion,
                    onItemClick = { item ->
                        coroutineScope.launch {
                            val id = when (item) {
                                is DrawerFeed -> item.id
                                is DrawerTag -> ID_UNSET
                                is DrawerTop -> ID_ALL_FEEDS
                            }
                            val tag = when (item) {
                                is DrawerFeed -> item.tag
                                is DrawerTag -> item.tag
                                is DrawerTop -> ""
                            }
                            onDrawerItemSelected(id, tag)
                            drawerState.close()
                        }
                    }
                )
            }
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedWithArticleScreen(
    viewState: FeedArticleScreenViewState,
    onRefreshVisible: () -> Unit,
    onRefreshAll: () -> Unit,
    onToggleOnlyUnread: (Boolean) -> Unit,
    onToggleOnlyBookmarked: (Boolean) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onShowToolbarMenu: (Boolean) -> Unit,
    ttsOnPlay: () -> Unit,
    ttsOnPause: () -> Unit,
    ttsOnStop: () -> Unit,
    ttsOnSkipNext: () -> Unit,
    ttsOnSelectLanguage: (LocaleOverride) -> Unit,
    onOpenInCustomTab: () -> Unit,
    onAddFeed: () -> Unit,
    onEditFeed: (Long) -> Unit,
    onShowEditDialog: () -> Unit,
    onDismissEditDialog: () -> Unit,
    onDeleteFeeds: (Iterable<Long>) -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onSettings: () -> Unit,
    onSendFeedback: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onShareArticle: () -> Unit,
    markAsUnread: (Long, Boolean) -> Unit,
    markBeforeAsRead: (Int) -> Unit,
    markAfterAsRead: (Int) -> Unit,
    onOpenFeedItem: (Long) -> Unit,
    onInteractWithList: () -> Unit,
    onInteractWithArticle: () -> Unit,
    onFeedTitleClick: () -> Unit,
    onToggleFullText: () -> Unit,
    displayFullText: () -> Unit,
    onToggleCurrentArticlePinned: () -> Unit,
    onSetPinned: (Long, Boolean) -> Unit,
    onToggleCurrentArticleBookmarked: () -> Unit,
    onSetBookmarked: (Long, Boolean) -> Unit,
    feedListState: LazyListState,
    articleListState: LazyListState,
    pagedFeedItems: LazyPagingItems<FeedListItem>,
) {
    val showingUnreadStateLabel = if (viewState.onlyUnread) {
        stringResource(R.string.showing_only_unread_articles)
    } else {
        stringResource(R.string.showing_all_articles)
    }

    val showingBookmarksStateLabel = if (viewState.onlyBookmarked) {
        stringResource(R.string.showing_only_bookmarked_articles)
    } else {
        stringResource(R.string.showing_all_articles)
    }

    val coroutineScope = rememberCoroutineScope()

    FeedScreen(
        viewState = viewState,
        onRefreshVisible = onRefreshVisible,
        onOpenNavDrawer = {
            coroutineScope.launch {
                if (drawerState.isOpen) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        },
        onMarkAllAsRead = onMarkAllAsRead,
        ttsOnPlay = ttsOnPlay,
        ttsOnPause = ttsOnPause,
        ttsOnStop = ttsOnStop,
        ttsOnSkipNext = ttsOnSkipNext,
        ttsOnSelectLanguage = ttsOnSelectLanguage,
        onDismissDeleteDialog = onDismissDeleteDialog,
        onDismissEditDialog = onDismissEditDialog,
        onDelete = onDeleteFeeds,
        onEditFeed = onEditFeed,
        toolbarActions = {
            IconToggleButton(
                checked = viewState.onlyUnread,
                onCheckedChange = onToggleOnlyUnread,
                modifier = Modifier.semantics {
                    stateDescription = showingUnreadStateLabel
                }
            ) {
                if (viewState.onlyUnread) {
                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                    )
                }
            }
            IconToggleButton(
                checked = viewState.onlyBookmarked,
                onCheckedChange = onToggleOnlyBookmarked,
                modifier = Modifier.semantics {
                    stateDescription = showingBookmarksStateLabel
                }
            ) {
                if (viewState.onlyBookmarked) {
                    Icon(
                        Icons.Default.BookmarkRemove,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = null,
                    )
                }
            }

            Box {
                IconButton(onClick = { onShowToolbarMenu(true) }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.open_menu),
                    )
                }
                DropdownMenu(
                    expanded = viewState.showToolbarMenu,
                    onDismissRequest = { onShowToolbarMenu(false) }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onMarkAllAsRead()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.mark_all_as_read))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onRefreshAll()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.synchronize_feeds),
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.synchronize_feeds))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onAddFeed()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.add_feed))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            if (viewState.visibleFeeds.size == 1) {
                                onEditFeed(viewState.visibleFeeds.first().id)
                            } else {
                                onShowEditDialog()
                            }
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.edit_feed))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowDeleteDialog()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.delete_feed))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onToggleFullText()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Article,
                                contentDescription = stringResource(R.string.fetch_full_article),
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.fetch_full_article))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onOpenInCustomTab()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                contentDescription = stringResource(R.string.open_in_web_view),
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.open_in_web_view))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onShareArticle()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.share))
                        }
                    )

                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            markAsUnread(viewState.articleId, true)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.VisibilityOff,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.mark_as_unread))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onToggleCurrentArticlePinned()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(
                                stringResource(
                                    if (viewState.isPinned) {
                                        R.string.unpin_article
                                    } else {
                                        R.string.pin_article
                                    }
                                )
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onToggleCurrentArticleBookmarked()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(
                                stringResource(
                                    if (viewState.isBookmarked) {
                                        R.string.remove_bookmark
                                    } else {
                                        R.string.bookmark_article
                                    }
                                )
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            ttsOnPlay()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.CustomFilled.TextToSpeech,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.read_article))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onImport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ImportExport,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.import_feeds_from_opml))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onExport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ImportExport,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.export_feeds_to_opml))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onSettings()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.action_settings))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onSendFeedback()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.send_bug_report))
                        }
                    )
                }
            }
        },
        content = { modifier ->
            Row(modifier) {
                FeedListContent(
                    viewState = viewState,
                    onOpenNavDrawer = {
                        coroutineScope.launch {
                            if (drawerState.isOpen) {
                                drawerState.close()
                            } else {
                                drawerState.open()
                            }
                        }
                    },
                    onAddFeed = onAddFeed,
                    markAsUnread = markAsUnread,
                    markBeforeAsRead = markBeforeAsRead,
                    markAfterAsRead = markAfterAsRead,
                    onItemClick = onOpenFeedItem,
                    listState = feedListState,
                    onSetPinned = onSetPinned,
                    onSetBookmarked = onSetBookmarked,
                    pagedFeedItems = pagedFeedItems,
                    modifier = Modifier
                        .width(334.dp)
                        .notifyInput(onInteractWithList),
                )
                if (viewState.articleId > ID_UNSET) {
                    // Avoid state sharing between articles
                    key(viewState.articleId) {
                        ArticleContent(
                            viewState = viewState,
                            screenType = ScreenType.DUAL,
                            onFeedTitleClick = onFeedTitleClick,
                            articleListState = articleListState,
                            displayFullText = displayFullText,
                            modifier = Modifier
                                .fillMaxSize()
                                .notifyInput(onInteractWithArticle)
                            //                            .imePadding() // add padding for the on-screen keyboard
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FeedListScreen(
    viewState: FeedScreenViewState,
    onRefreshVisible: () -> Unit,
    onRefreshAll: () -> Unit,
    onToggleOnlyUnread: (Boolean) -> Unit,
    onToggleOnlyBookmarked: (Boolean) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onShowToolbarMenu: (Boolean) -> Unit,
    ttsOnPlay: () -> Unit,
    ttsOnPause: () -> Unit,
    ttsOnStop: () -> Unit,
    ttsOnSkipNext: () -> Unit,
    ttsOnSelectLanguage: (LocaleOverride) -> Unit,
    onAddFeed: () -> Unit,
    onEditFeed: (Long) -> Unit,
    onShowEditDialog: () -> Unit,
    onDismissEditDialog: () -> Unit,
    onDeleteFeeds: (Iterable<Long>) -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onSettings: () -> Unit,
    onSendFeedback: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    markAsUnread: (Long, Boolean) -> Unit,
    markBeforeAsRead: (Int) -> Unit,
    markAfterAsRead: (Int) -> Unit,
    onOpenFeedItem: (Long) -> Unit,
    onSetPinned: (Long, Boolean) -> Unit,
    onSetBookmarked: (Long, Boolean) -> Unit,
    feedListState: LazyListState,
    feedGridState: LazyStaggeredGridState,
    pagedFeedItems: LazyPagingItems<FeedListItem>,
    feedArticleScreenType: FeedArticleScreenType,
) {
    val showingUnreadStateLabel = if (viewState.onlyUnread) {
        stringResource(R.string.showing_only_unread_articles)
    } else {
        stringResource(R.string.showing_all_articles)
    }

    val showingBookmarksStateLabel = if (viewState.onlyBookmarked) {
        stringResource(R.string.showing_only_bookmarked_articles)
    } else {
        stringResource(R.string.showing_all_articles)
    }

    val coroutineScope = rememberCoroutineScope()

    FeedScreen(
        viewState = viewState,
        onRefreshVisible = onRefreshVisible,
        onOpenNavDrawer = {
            coroutineScope.launch {
                if (drawerState.isOpen) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        },
        onMarkAllAsRead = onMarkAllAsRead,
        ttsOnPlay = ttsOnPlay,
        ttsOnPause = ttsOnPause,
        ttsOnStop = ttsOnStop,
        ttsOnSkipNext = ttsOnSkipNext,
        ttsOnSelectLanguage = ttsOnSelectLanguage,
        onDismissDeleteDialog = onDismissDeleteDialog,
        onDismissEditDialog = onDismissEditDialog,
        onDelete = onDeleteFeeds,
        onEditFeed = onEditFeed,
        toolbarActions = {
            IconToggleButton(
                checked = viewState.onlyUnread,
                onCheckedChange = onToggleOnlyUnread,
                modifier = Modifier.semantics {
                    stateDescription = showingUnreadStateLabel
                }
            ) {
                if (viewState.onlyUnread) {
                    Icon(
                        Icons.Default.VisibilityOff,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                    )
                }
            }
            IconToggleButton(
                checked = viewState.onlyBookmarked,
                onCheckedChange = onToggleOnlyBookmarked,
                modifier = Modifier.semantics {
                    stateDescription = showingBookmarksStateLabel
                }
            ) {
                if (viewState.onlyBookmarked) {
                    Icon(
                        Icons.Default.BookmarkRemove,
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        Icons.Default.Bookmark,
                        contentDescription = null,
                    )
                }
            }

            Box {
                IconButton(onClick = { onShowToolbarMenu(true) }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.open_menu),
                    )
                }
                DropdownMenu(
                    expanded = viewState.showToolbarMenu,
                    onDismissRequest = { onShowToolbarMenu(false) }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onMarkAllAsRead()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.mark_all_as_read))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onRefreshAll()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.synchronize_feeds),
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.synchronize_feeds))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onAddFeed()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.add_feed))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            if (viewState.visibleFeeds.size == 1) {
                                onEditFeed(viewState.visibleFeeds.first().id)
                            } else {
                                onShowEditDialog()
                            }
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.edit_feed))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowDeleteDialog()
                            onShowToolbarMenu(false)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.delete_feed))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onImport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ImportExport,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.import_feeds_from_opml))
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onExport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ImportExport,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.export_feeds_to_opml))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onSettings()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.action_settings))
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            onShowToolbarMenu(false)
                            onSendFeedback()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                            )
                        },
                        text = {
                            Text(stringResource(id = R.string.send_bug_report))
                        }
                    )
                }
            }
        },
    ) { modifier ->
        if (feedArticleScreenType == FeedArticleScreenType.FeedGrid) {
            FeedGridContent(
                viewState = viewState,
                gridState = feedGridState,
                onOpenNavDrawer = {
                    coroutineScope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        } else {
                            drawerState.open()
                        }
                    }
                },
                onAddFeed = onAddFeed,
                markBeforeAsRead = markBeforeAsRead,
                markAfterAsRead = markAfterAsRead,
                onItemClick = onOpenFeedItem,
                onSetPinned = onSetPinned,
                onSetBookmarked = onSetBookmarked,
                pagedFeedItems = pagedFeedItems,
                modifier = modifier,
            )
        } else {
            FeedListContent(
                viewState = viewState,
                onOpenNavDrawer = {
                    coroutineScope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        } else {
                            drawerState.open()
                        }
                    }
                },
                onAddFeed = onAddFeed,
                markAsUnread = markAsUnread,
                markBeforeAsRead = markBeforeAsRead,
                markAfterAsRead = markAfterAsRead,
                onItemClick = onOpenFeedItem,
                listState = feedListState,
                onSetPinned = onSetPinned,
                onSetBookmarked = onSetBookmarked,
                pagedFeedItems = pagedFeedItems,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    viewState: ArticleScreenViewState,
    onToggleFullText: () -> Unit,
    onMarkAsUnread: () -> Unit,
    onShare: () -> Unit,
    onOpenInCustomTab: () -> Unit,
    onFeedTitleClick: () -> Unit,
    onShowToolbarMenu: (Boolean) -> Unit,
    onInteractWithArticle: () -> Unit,
    displayFullText: () -> Unit,
    ttsOnPlay: () -> Unit,
    ttsOnPause: () -> Unit,
    ttsOnStop: () -> Unit,
    ttsOnSkipNext: () -> Unit,
    ttsOnSelectLanguage: (LocaleOverride) -> Unit,
    onTogglePinned: () -> Unit,
    onToggleBookmarked: () -> Unit,
    articleListState: LazyListState,
    onNavigateUp: () -> Unit,
) {
    BackHandler(onBack = onNavigateUp)
    val scrollBehavior = dynamicScrollBehavior()

    SetStatusBarColorToMatchScrollableTopAppBar(scrollBehavior)

    val bottomBarVisibleState = remember { MutableTransitionState(viewState.isBottomBarVisible) }
    LaunchedEffect(viewState.isBottomBarVisible) {
        bottomBarVisibleState.targetState = viewState.isBottomBarVisible
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            DynamicTopAppBar(
                scrollBehavior = scrollBehavior,
                title = viewState.feedDisplayTitle,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleFullText
                    ) {
                        Icon(
                            Icons.Default.Article,
                            contentDescription = stringResource(R.string.fetch_full_article)
                        )
                    }

                    IconButton(onClick = onOpenInCustomTab) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = stringResource(id = R.string.open_in_web_view)
                        )
                    }

                    Box {
                        IconButton(onClick = { onShowToolbarMenu(true) }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.open_menu),
                            )
                        }
                        DropdownMenu(
                            expanded = viewState.showToolbarMenu,
                            onDismissRequest = { onShowToolbarMenu(false) }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    onShowToolbarMenu(false)
                                    onShare()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = null,
                                    )
                                },
                                text = {
                                    Text(stringResource(id = R.string.share))
                                }
                            )

                            DropdownMenuItem(
                                onClick = {
                                    onShowToolbarMenu(false)
                                    onMarkAsUnread()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                    )
                                },
                                text = {
                                    Text(stringResource(id = R.string.mark_as_unread))
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    onShowToolbarMenu(false)
                                    onTogglePinned()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.PushPin,
                                        contentDescription = null,
                                    )
                                },
                                text = {
                                    Text(
                                        stringResource(
                                            if (viewState.isPinned) {
                                                R.string.unpin_article
                                            } else {
                                                R.string.pin_article
                                            }
                                        )
                                    )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    onShowToolbarMenu(false)
                                    onToggleBookmarked()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Bookmark,
                                        contentDescription = null,
                                    )
                                },
                                text = {
                                    Text(
                                        stringResource(
                                            if (viewState.isBookmarked) {
                                                R.string.remove_bookmark
                                            } else {
                                                R.string.bookmark_article
                                            }
                                        )
                                    )
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    onShowToolbarMenu(false)
                                    ttsOnPlay()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.CustomFilled.TextToSpeech,
                                        contentDescription = null,
                                    )
                                },
                                text = {
                                    Text(stringResource(id = R.string.read_article))
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            HideableTTSPlayer(
                visibleState = bottomBarVisibleState,
                currentlyPlaying = viewState.isTTSPlaying,
                onPlay = ttsOnPlay,
                onPause = ttsOnPause,
                onStop = ttsOnStop,
                onSkipNext = ttsOnSkipNext,
                languages = ImmutableHolder(viewState.ttsLanguages),
                onSelectLanguage = ttsOnSelectLanguage,
            )
        },
    ) { padding ->
        ArticleContent(
            viewState = viewState,
            screenType = ScreenType.SINGLE,
            articleListState = articleListState,
            onFeedTitleClick = onFeedTitleClick,
            displayFullText = displayFullText,
            modifier = Modifier
                .padding(padding)
                .notifyInput(onInteractWithArticle)
        )
    }
}

@Composable
fun ArticleContent(
    viewState: ArticleScreenViewState,
    screenType: ScreenType,
    onFeedTitleClick: () -> Unit,
    articleListState: LazyListState,
    displayFullText: () -> Unit,
    modifier: Modifier,
) {
    val isLightTheme = MaterialTheme.colorScheme.isLight

    @DrawableRes
    val placeHolder: Int by remember(isLightTheme) {
        derivedStateOf {
            if (isLightTheme) {
                R.drawable.placeholder_image_article_day
            } else {
                R.drawable.placeholder_image_article_night
            }
        }
    }

    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    val context = LocalContext.current

    if (viewState.articleId > ID_UNSET &&
        viewState.textToDisplay == TextToDisplay.FULLTEXT &&
        !blobFullFile(viewState.articleId, context.filesDir).isFile
    ) {
        LaunchedEffect(viewState.articleId, viewState.textToDisplay) {
            // Trigger parse and fetch
            displayFullText()
        }
    }

    ReaderView(
        modifier = modifier,
        screenType = screenType,
        articleListState = articleListState,
        articleTitle = viewState.articleTitle,
        feedTitle = viewState.feedDisplayTitle,
        enclosure = viewState.enclosure,
        onEnclosureClick = {
            if (viewState.enclosure.present) {
                openLinkInBrowser(context, viewState.enclosure.link)
            }
        },
        onFeedTitleClick = onFeedTitleClick,
        authorDate = when {
            viewState.author == null && viewState.pubDate != null ->
                stringResource(
                    R.string.on_date,
                    (viewState.pubDate ?: ZonedDateTime.now()).format(dateTimeFormat)
                )
            viewState.author != null && viewState.pubDate != null ->
                stringResource(
                    R.string.by_author_on_date,
                    // Must wrap author in unicode marks to ensure it formats
                    // correctly in RTL
                    context.unicodeWrap(viewState.author ?: ""),
                    (viewState.pubDate ?: ZonedDateTime.now()).format(dateTimeFormat)
                )
            else -> null
        },
    ) {
        // Can take a composition or two before viewstate is set to its actual values
        // TODO show something in case no article to show
        if (viewState.articleId > ID_UNSET) {
            when (viewState.textToDisplay) {
                TextToDisplay.DEFAULT -> {
                    if (blobFile(viewState.articleId, context.filesDir).isFile) {
                        blobInputStream(viewState.articleId, context.filesDir).use {
                            htmlFormattedText(
                                inputStream = it,
                                baseUrl = viewState.articleFeedUrl ?: "",
                                imagePlaceholder = placeHolder,
                                onLinkClick = { link ->
                                    onLinkClick(
                                        link = link,
                                        linkOpener = viewState.linkOpener,
                                        context = context,
                                        toolbarColor = toolbarColor
                                    )
                                }
                            )
                        }
                    } else {
                        item {
                            Text(text = stringResource(id = R.string.failed_to_open_article))
                        }
                    }
                }
                TextToDisplay.FAILED_TO_LOAD_FULLTEXT -> {
                    item {
                        Text(text = stringResource(id = R.string.failed_to_fetch_full_article))
                    }
                }
                TextToDisplay.LOADING_FULLTEXT -> {
                    LoadingItem()
                }

                TextToDisplay.FULLTEXT -> {
                    if (blobFullFile(viewState.articleId, context.filesDir).isFile) {
                        blobFullInputStream(viewState.articleId, context.filesDir).use {
                            htmlFormattedText(
                                inputStream = it,
                                baseUrl = viewState.articleFeedUrl ?: "",
                                imagePlaceholder = placeHolder,
                                onLinkClick = { link ->
                                    onLinkClick(
                                        link = link,
                                        linkOpener = viewState.linkOpener,
                                        context = context,
                                        toolbarColor = toolbarColor
                                    )
                                }
                            )
                        }
                    } else {
                        // Already trigger load in effect above
                        LoadingItem()
                    }
                }
            }
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.LoadingItem() {
    item {
        Text(text = stringResource(id = R.string.fetching_full_article))
    }
}

enum class FeedArticleScreenType {
    FeedGrid,
    Feed,
    ArticleDetails,
}

private fun getFeedArticleScreenType(
    windowSize: WindowSize,
    viewState: FeedArticleScreenViewState,
): FeedArticleScreenType = when (getScreenType(windowSize)) {
    ScreenType.SINGLE -> {
        when {
            viewState.isArticleOpen -> FeedArticleScreenType.ArticleDetails
            else -> FeedArticleScreenType.Feed
        }
    }
    ScreenType.DUAL -> {
        when {
            viewState.isArticleOpen -> FeedArticleScreenType.ArticleDetails
            else -> FeedArticleScreenType.FeedGrid
        }
    }
}

/**
 * A [Modifier] that tracks all input, and calls [block] every time input is received.
 */
private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }
