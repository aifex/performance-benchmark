package main

import (
	"fmt"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	// "errors"
)

type Invoice struct {
	Id            int    `json:"id" gorm:"primaryKey"`
	InvoiceNumber string `json:"invoiceNumber"`
	Amount        int    `json:"amount"`
}

var Db *gorm.DB

func initDb() *gorm.DB {
	dsn := "host=localhost user=irembodbuser password=p@ssw0rd dbname=performancetestgo port=5432 sslmode=disable"
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		log.Fatal(err)
	}
	db.AutoMigrate(Invoice{})
	return db
}

func getInvoices(c *gin.Context) {
	var invoices []Invoice
	result := Db.Find(&invoices)
	if result.Error != nil {
		fmt.Println(result.Error)
	}
	c.IndentedJSON(http.StatusOK, invoices)
}

func getInvoiceMetrics(c *gin.Context) {
	var invoices []Invoice
	result := Db.Order("amount").Find(&invoices)
	if result.Error != nil {
		fmt.Println(result.Error)
	}
	sum := 0
	for i := 0; i < len(invoices); i++ {
		sum += invoices[i].Amount
	}

	avg := (float64(sum)) / (float64(len(invoices)))

	c.IndentedJSON(http.StatusOK, gin.H{"highestAmount": invoices[len(invoices)-1].Amount,
		"lowestAmount": invoices[0].Amount, "averageAmount": avg, "totalAmount": sum})
}

func getInvoiceByInvoiceNumber(c *gin.Context) {
	var inv Invoice
	invoiceNumber := c.Param("invoiceNumber")
	result := Db.Model(&Invoice{}).Where("invoice_number = ?", invoiceNumber).First(&inv)
	if result.Error != nil {
		fmt.Println(result.Error)
	}
	c.IndentedJSON(http.StatusOK, inv)
}

func createInvoice(c *gin.Context) {
	var newInvoice Invoice
	if err := c.BindJSON(&newInvoice); err != nil {
		fmt.Println(err)
		return
	}

	result := Db.Create(&newInvoice)
	if result.Error != nil {
		fmt.Println(result.Error)
	}

	c.IndentedJSON(http.StatusCreated, newInvoice)
}

func main() {

	Db = initDb()

	router := gin.Default()
	router.GET("/invoices", getInvoices)
	router.POST("/invoices", createInvoice)
	router.GET("/invoices/:invoiceNumber", getInvoiceByInvoiceNumber)
	router.GET("/invoice-metrics", getInvoiceMetrics)
	router.Run("localhost:9040")
}
