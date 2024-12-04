package com.cia103g5.article.controller;

import com.cia103g5.article.model.Post; // 引入 Post 類別，對應文章的資料表結構
import com.cia103g5.article.model.PostCategory; // 引入 PostCategory 類別，對應文章分類的資料表結構
import com.cia103g5.article.model.PostPic;
import com.cia103g5.article.repository.PostRepository; // 引入 PostRepository，提供與 Post 相關的資料庫操作
import com.cia103g5.article.repository.PostCategoryRepository; // 引入 PostCategoryRepository，提供與 PostCategory 相關的資料庫操作
import com.cia103g5.article.repository.PostPicRepository;

import org.springframework.beans.factory.annotation.Autowired; // 引入 Autowired，用於依賴注入，讓 Spring 自動注入需要的 Repository
import org.springframework.http.ResponseEntity; // 引入 ResponseEntity，用於構建 HTTP 回應
import org.springframework.stereotype.Controller; // 引入 Controller 註解，將此類別標記為 Spring 的 Controller
import org.springframework.ui.Model; // 引入 Model，用於將數據傳遞到前端模板
import org.springframework.web.bind.annotation.*; // 引入所有 RequestMapping 註解，用於處理不同的 HTTP 請求
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime; // 引入 LocalDateTime，用於處理時間和日期
import java.time.format.DateTimeFormatter;
import java.util.List; // 引入 List，用於處理資料清單
import java.util.Map; // 引入 Map，用於接收 JSON 格式的請求資料
import java.util.Optional; // 引入 Optional，用於處理可能為空的查詢結果
import org.springframework.data.domain.Sort; // 引入 Sort，用於設定排序條件

@Controller
public class PageController {
	@Autowired
	private PostPicRepository postPicRepository;

	@Autowired
	private PostRepository postRepository; // 自動注入 PostRepository，提供操作 Post 表的功能

	@Autowired
	private PostCategoryRepository postCategoryRepository; // 自動注入 PostCategoryRepository，提供操作 PostCategory 表的功能

	/**
	 * 顯示發文頁面，處理 GET 請求，並設置路徑為 /articles 當用戶訪問 /articles 時，顯示發文頁面，並加載 layout.html
	 * 作為主模板。 layout.html 中的片段選擇會根據 page 變數來決定。
	 */
	@GetMapping("/articles")
	public String showArticles(Model model) {
		model.addAttribute("page", "articles"); // 設定 page 變數為 "articles"，讓 layout.html 加載 articles 的片段
		System.out.println("Page set to articles"); // 日誌輸出確認 page 設定為 "articles"
		return "layout"; // 返回 layout.html 作為主模板，該模板會包含 articles 的內容
	}

	/**
	 * 顯示熱門文章頁面，按讚數降冪排序，讚數相同的文章按創建時間降冪排序
	 * 
	 * @param model 用於將查詢結果傳遞到前端模板
	 * @return 返回 layout.html 作為主模板，顯示熱門文章頁面
	 */
	@GetMapping("/popular")
	public String popularPage(Model model) {
		// 查詢所有文章，按 totalLikes 降冪排序，若讚數相同則按創建時間降冪排序
		List<Post> posts = postRepository.findAll(Sort.by(Sort.Order.desc("totalLikes"), Sort.Order.desc("postTime")));

		// 將排序後的文章列表傳遞到模板
		model.addAttribute("posts", posts); // 將文章列表傳遞到模板
		model.addAttribute("page", "popular"); // 設定 page 變數為 "popular"，讓 layout.html 加載 popular 的片段
		System.out.println("Page set to popular"); // 日誌輸出確認 page 設定為 "popular"

		return "layout"; // 返回 layout.html 作為主模板，該模板會包含熱門文章的內容
	}

	/**
	 * 顯示論壇頁面，處理 GET 請求，並設置路徑為 /forum 當用戶訪問 /forum 時，顯示論壇頁面，並加載 layout.html 作為主模板。
	 * layout.html 中的片段選擇會根據 page 變數來決定。
	 */
	@GetMapping("/forum")
	public String getForum(Model model) {
		// 查詢所有文章
		List<Post> posts = postRepository.findAll();

		// 格式化文章的發表時間
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		posts.forEach(post -> {
			if (post.getPostTime() != null) {
				post.setFormattedPostTime(post.getPostTime().format(formatter));
			}
		});

		model.addAttribute("posts", posts);
		model.addAttribute("page", "forum"); // 設置頁面屬性
		return "layout"; // 返回 layout.html 作為主模板
	}


	/**
	 * 處理文章的提交請求 (接收 JSON 格式資料)，處理 POST 請求，並設置路徑為 /articles 當用戶發送文章數據至 /articles
	 * 時，系統會驗證並儲存該文章。
	 */
	@PostMapping("/articles")
	public String createArticle(@RequestBody Map<String, Object> payload, RedirectAttributes redirectAttributes) {
		// 紀錄接收到的請求
		System.out.println("Received POST request to /articles");

		// 從 JSON 載荷中提取資料
		String title = (String) payload.get("title"); // 文章標題
		String authorName = (String) payload.get("authorName"); // 作者名稱
		String content = (String) payload.get("content"); // 文章內容
		String category = (String) payload.get("category"); // 分類名稱

		// 檢查所有必填欄位是否填寫
		if (title == null || title.isEmpty() || authorName == null || authorName.isEmpty() || content == null
				|| content.isEmpty() || category == null || category.isEmpty()) {
			// 添加錯誤訊息，並跳轉回 /articles 頁面
			redirectAttributes.addFlashAttribute("errorMessage", "所有欄位均為必填");
			return "redirect:/articles";
		}

		try {
			// 檢查分類名稱是否有效
			Optional<PostCategory> postCategoryOpt = postCategoryRepository.findByCategoryName(category);
			if (!postCategoryOpt.isPresent()) {
				// 若分類無效，返回錯誤訊息並重新導向
				redirectAttributes.addFlashAttribute("errorMessage", "無效的分類名稱");
				return "redirect:/articles";
			}

			// 獲取分類物件
			PostCategory postCategory = postCategoryOpt.get();

			// 建立新文章物件並設定屬性
			Post post = new Post();
			post.setTitle(title);
			post.setAuthorName(authorName);
			post.setContent(content);
			post.setPostCategory(postCategory); // 設置文章的分類
			post.setModTime(LocalDateTime.now()); // 設定文章最後修改時間
			post.setPostTime(LocalDateTime.now()); // 設定文章發布時間
			post.setUserType((byte) 1); // 假設 user_type 為 1
			post.setMemId(6); // 假設 mem_id 為 6

			// 儲存文章至資料庫
			postRepository.save(post);

			// 紀錄成功訊息
			System.out.println("Saved Post: " + post);

			// 添加成功訊息，並跳轉至 /forum
			redirectAttributes.addFlashAttribute("successMessage", "文章已成功發表！");
			return "redirect:/forum";

		} catch (Exception e) {
			// 若發生例外，記錄錯誤並回傳錯誤訊息
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "儲存文章時發生錯誤");
			return "redirect:/articles";
		}
	}
//RedirectAttributes

//使用 RedirectAttributes 傳遞訊息（例如錯誤或成功提示）給下一個頁面。
//避免在 URL 中直接顯示錯誤訊息，提升用戶體驗。

	/**
	 * 顯示指定分類的文章
	 * 
	 * @param categoryName 分類名稱
	 * @param model        用於將數據傳遞到前端模板
	 * @return 包含分類文章的模板頁面
	 */
	@GetMapping("/category/{categoryName}")
	public String getPostsByCategory(@PathVariable("categoryName") String categoryName, Model model) {
	    PostCategory category = postCategoryRepository.findByCategoryName(categoryName).orElse(null);
	    if (category != null) {
	        List<Post> posts = postRepository.findByPostCategoryOrderByPostTimeDesc(category); // 根據分類查詢文章並按時間降冪排序
	        model.addAttribute("posts", posts); // 將文章列表傳遞到前端
	        model.addAttribute("categoryName", categoryName); // 將分類名稱傳遞到前端
	    } else {
	        model.addAttribute("posts", List.of()); // 如果分類不存在，返回空列表
	        model.addAttribute("categoryName", "未知分類");
	    }
	    model.addAttribute("page", "category"); // 設定 page 變數為 "category"
	    return "layout"; // 使用 layout.html 作為主模板
	}


	/**
	 * 顯示文章詳細頁面
	 * 
	 * @param postNo 文章的 ID
	 * @param model  用於傳遞數據到模板
	 * @return 返回 layout.html 作為主模板，顯示文章詳細內容
	 */
	@GetMapping("/post/{postNo}")
	public String getPostDetail(@PathVariable("postNo") Integer postNo, Model model) {
		Optional<Post> postOptional = postRepository.findById(postNo);
		if (postOptional.isPresent()) {
			Post post = postOptional.get();
			model.addAttribute("post", post);

			// 格式化發表時間
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedPostTime = post.getPostTime().format(formatter);
			model.addAttribute("formattedPostTime", formattedPostTime);

			List<PostPic> pics = postPicRepository.findByPostNo(postNo);
			model.addAttribute("pics", pics);

			model.addAttribute("page", "articlesdetail"); // 設置頁面屬性
			return "layout"; // 返回 layout.html 作為主模板
		} else {
			return "error/404"; // 返回 404 頁面
		}
	}

}
