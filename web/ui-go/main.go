package main

import (
	"context"
	_ "embed"
	"flag"
	"fmt"
	"html/template"
	"io"
	"log"
	"net/http"

	enolagrpc "dev/enola/grpc"
)

const (
	uiEntityRoute = "/ui/entity/"
)

//go:embed web/page.template
var templatePage string

func main() {
	addr := flag.String("addr", ":8080", "server address")
	enolaAddr := flag.String("enolaAddr", "localhost:9090", "enola address")

	err := run(*addr, *enolaAddr)
	if err != nil {
		log.Fatal(err)
	}
}

func run(addr, enolaAddr string) error {
	client, err := newClient(enolaAddr)
	if err != nil {
		return err
	}

	h := handler{client: client}
	defer func() { _ = h.client.close() }()

	http.HandleFunc(uiEntityRoute, h.entity)
	return http.ListenAndServe(addr, nil)
}

type enolaClient interface {
	getEntity(ctx context.Context, id string) (*enolagrpc.Entity, error)
	close() error
}

type handler struct {
	client enolaClient
}

func (h handler) entity(w http.ResponseWriter, r *http.Request) {
	id := r.URL.Path[len(uiEntityRoute):]

	entity, err := h.client.getEntity(r.Context(), id)
	if err != nil {
		handleError(w, err)
		return
	}

	if err := render(w, map[string]any{"entity": entity}); err != nil {
		handleError(w, err)
		return
	}
}

func render(w io.Writer, params map[string]any) error {
	tmpl, err := template.New("ui").Parse(templatePage)
	if err != nil {
		return fmt.Errorf("failed to parse template: %w", err)
	}

	err = tmpl.Execute(w, params)
	if err != nil {
		return fmt.Errorf("failed to execute template: %w", err)
	}
	return nil
}

func handleError(w http.ResponseWriter, err error) {
	w.WriteHeader(http.StatusInternalServerError)
	_, _ = w.Write([]byte(err.Error()))
}
