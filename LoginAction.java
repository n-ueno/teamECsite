package com.internousdev.oregon.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.oregon.dao.CartInfoDAO;
import com.internousdev.oregon.dao.UserInfoDAO;
import com.internousdev.oregon.dto.CartInfoDTO;
import com.internousdev.oregon.dto.UserInfoDTO;
import com.internousdev.oregon.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements SessionAware {
	private String userId;
	private String password;
	private boolean savedUserIdFlag;
	private List<String> userIdErrorMessageList;
	private List<String> passwordErrorMessageList;
	private String isNotUserInfoMessage;
	private List<CartInfoDTO> cartInfoDTOList;
	private int totalPrice;
	private Map<String, Object> session;

	public String execute() {
		// セッションタイムアウト(ログインしていないためuserIdは見ない)
		if(!session.containsKey("tempUserId")) {
			return "sessionTimeout";
		}

		if (session.containsKey("createUserFlag")) {
			userId = session.get("userIdForCreateUser").toString();
			password = session.get("password").toString();
			// ユーザー登録画面から遷移した場合にユーザーIdとパスワードがsessionに入っているため削除
			session.remove("userIdForCreateUser");
			session.remove("password");
			session.remove("familyName");
			session.remove("firstName");
			session.remove("familyNameKana");
			session.remove("firstNameKana");
			session.remove("sex");
			session.remove("sexList");
			session.remove("email");
			session.remove("createUserFlag");
		}

		String result = ERROR;

		// sessionに保存されたsavedUserIdFlagを削除する
		session.remove("savedUserIdFlag");

		InputChecker inputChecker = new InputChecker();
		userIdErrorMessageList = inputChecker.doCheck("ユーザーID", userId, 1, 8, true, false, false, true, false, false);
		passwordErrorMessageList = inputChecker.doCheck("パスワード", password, 1, 16, true, false, false, true, false, false);

		if(userIdErrorMessageList.size() > 0
				|| passwordErrorMessageList.size() > 0) {
			session.put("logined", 0);
			return result;
		}

		UserInfoDAO userInfoDAO = new UserInfoDAO();
		if(userInfoDAO.isExistsUserInfo(userId, password)
				&& userInfoDAO.login(userId, password) > 0) {
			CartInfoDAO cartInfoDAO = new CartInfoDAO();

			// カートの情報をユーザーに紐付ける。
			String tempUserId = session.get("tempUserId").toString();
			List<CartInfoDTO> cartInfoDTOListForTempUser =  cartInfoDAO.getCartInfoDTOList(tempUserId);
			if (cartInfoDTOListForTempUser != null) {
				boolean cartresult = changeCartInfo(cartInfoDTOListForTempUser, tempUserId);
				if (!cartresult) {
					return "DBError";
				}
			}
			// 次の遷移先を設定
			if (session.containsKey("cartFlag")) {
				session.remove("cartFlag");
				result = "cart";
			} else {
				result = SUCCESS;
			}

			// ユーザー情報をsessionに登録し、tempUserIdを削除する。
			UserInfoDTO userInfoDTO = userInfoDAO.getUserInfo(userId, password);
			session.put("userId", userInfoDTO.getUserId());
			session.put("logined", 1);
			if (savedUserIdFlag) {
				session.put("savedUserIdFlag", true);
			}
			session.remove("tempUserId");
		} else {
			isNotUserInfoMessage = "ユーザーIDまたはパスワードが異なります。";
		}
		return result;
	}

	/**
	 * DBのカート情報を更新/作成する
	 * @param cartInfoDTOListForTempUser 仮ユーザーに紐づくカート情報
	 * @param tempUserId 仮ユーザーID
	 * @return
	 */
	private boolean changeCartInfo(List<CartInfoDTO> cartInfoDTOListForTempUser, String tempUserId) {
		int count = 0;
		CartInfoDAO cartInfoDAO = new CartInfoDAO();
		boolean result = false;

		for (CartInfoDTO dto : cartInfoDTOListForTempUser) {
			// 処理対象のカート情報とDBのカート情報テーブルにユーザーIDに紐づく同じ商品IDのカート情報が存在するかチェックする。
			if (cartInfoDAO.isExistsCartInfo(userId, dto.getProductId())) {
				// 同じ商品IDのカート情報が存在する場合は、処理対象のカート情報の個数を既に存在するカート情報の個数に足し、tempUserIdのデータは削除する。
				count += cartInfoDAO.updateProductCount(userId, dto.getProductId(), dto.getProductCount());
				cartInfoDAO.delete(String.valueOf(dto.getProductId()), tempUserId);
			} else {
				// 同じ商品IDのカート情報が存在しない場合は、処理対象のカート情報のユーザーIDをログインするユーザーのユーザーIDに更新する。
				count += cartInfoDAO.linkToUserId(tempUserId,userId, dto.getProductId());
			}
		}

		if (count == cartInfoDTOListForTempUser.size()) {
			cartInfoDTOList = cartInfoDAO.getCartInfoDTOList(userId);
			totalPrice = cartInfoDAO.getTotalPrice(userId);
			result = true;
		}
		return result;

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSavedUserIdFlag() {
		return savedUserIdFlag;
	}

	public void setSavedUserIdFlag(boolean savedUserIdFlag) {
		this.savedUserIdFlag = savedUserIdFlag;
	}

	public List<String> getUserIdErrorMessageList() {
		return userIdErrorMessageList;
	}

	public void setUserIdErrorMessageList(List<String> userIdErrorMessageList) {
		this.userIdErrorMessageList = userIdErrorMessageList;
	}
	public List<String> getPasswordErrorMessageList() {
		return passwordErrorMessageList;
	}
	public void setPasswordErrorMessageList(List<String> passwordErrorMessageList) {
		this.passwordErrorMessageList = passwordErrorMessageList;
	}
	public String getIsNotUserInfoMessage() {
		return isNotUserInfoMessage;
	}

	public void setIsNotUserInfoMessage(String isNotUserInfoMessage) {
		this.isNotUserInfoMessage = isNotUserInfoMessage;
	}

	public List<CartInfoDTO> getCartInfoDTOList() {
		return cartInfoDTOList;
	}

	public void setCartInfoDTOList(List<CartInfoDTO> cartInfoDTOList) {
		this.cartInfoDTOList = cartInfoDTOList;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Map<String, Object> getSession() {
		return session;
	}
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
